//=============================================================================
//===	Copyright (C) 2001-2011 Food and Agriculture Organization of the
//===	United Nations (FAO-UN), United Nations World Food Programme (WFP)
//===	and United Nations Environment Programme (UNEP)
//===
//===	This program is free software; you can redistribute it and/or modify
//===	it under the terms of the GNU General Public License as published by
//===	the Free Software Foundation; either version 2 of the License, or (at
//===	your option) any later version.
//===
//===	This program is distributed in the hope that it will be useful, but
//===	WITHOUT ANY WARRANTY; without even the implied warranty of
//===	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//===	General Public License for more details.
//===
//===	You should have received a copy of the GNU General Public License
//===	along with this program; if not, write to the Free Software
//===	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
//===
//===	Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
//===	Rome - Italy. email: geonetwork@osgeo.org
//==============================================================================

package org.fao.geonet.services.metadata;

import jeeves.resources.dbms.Dbms;
import jeeves.server.UserSession;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Xml;

import org.fao.geonet.constants.Edit;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.constants.Params;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.kernel.AccessManager;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.kernel.setting.SettingManager;
import org.fao.geonet.util.MailSender;
import org.fao.geonet.util.ISODate;

import org.jdom.Element;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

public class DefaultStatusActions implements StatusActions {

	private String host, port, from, fromDescr, replyTo, replyToDescr;
	private ServiceContext context;
	private AccessManager am;
	private DataManager dm;
	private Dbms dbms;
	private String siteUrl;
	private UserSession session;
	private boolean emailNotes = true;
	private static boolean html5ui = true;

	private String allGroup = "1";

	/**
		* Constructor.
		*/
	public DefaultStatusActions() {}

	/** 
	  * Initializes the StatusActions class with external info from GeoNetwork.
	  *
		* @param context
		* @param dbms
		*/
	public void init(ServiceContext context, Dbms dbms) {
	
		this.context = context;
		this.dbms = dbms;

		GeonetContext  gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
		SettingManager sm = gc.getSettingManager();
		am = gc.getAccessManager();

		host = sm.getValue("system/feedback/mailServer/host");
		port = sm.getValue("system/feedback/mailServer/port");
		from = sm.getValue("system/feedback/email");

		if (host.length() == 0) {
			context.error("Mail server host not configured, email notifications won't be sent.");
			emailNotes = false;
		}
		
		if (port.length() == 0) {
			context.error("Mail server port not configured, email notifications won't be sent.");
			emailNotes = false;
		}
		
		if (from.length() == 0) {
			context.error("Mail feedback address not configured, email notifications won't be sent.");
			emailNotes = false;
		}

		fromDescr = "Metadata Workflow";

		session = context.getUserSession();
		replyTo = session.getEmailAddr();
		if (replyTo != null) {
			replyToDescr = session.getName() + " " + session.getSurname();
		} else {
			replyTo = from; replyToDescr = fromDescr;
		}


		dm = gc.getDataManager();
		siteUrl = dm.getSiteURL();
	}

	/** 
	  * Called when a record is edited to set/reset status.
	  *
		* @param id The metadata id that has been edited.
		* @param minorEdit If true then the edit was a minor edit. (The new angular editor doesn't provide this flag so it is 
		* always false)
		*/
	public void onEdit(int id, boolean minorEdit) throws Exception {

		if (!minorEdit && dm.getCurrentStatus(dbms, id).equals(Params.Status.APPROVED)) {
			String changeMessage = "GeoNetwork user "+session.getUserId()+" ("+session.getUsername()+") edited approved metadata record "+id+", status will be set to DRAFT";
			unsetAllOperations(id);
			dm.setStatus(context, dbms, id, Integer.valueOf(Params.Status.DRAFT), new ISODate().toString(), changeMessage);
		}
		
		if (!minorEdit && dm.getCurrentStatus(dbms, id).equals(Params.Status.REJECTED)) {
			String changeMessage = "GeoNetwork user "+session.getUserId()+" ("+session.getUsername()+") edited rejected metadata record "+id+", status will be set to DRAFT";
			dm.setStatus(context, dbms, id, Integer.valueOf(Params.Status.DRAFT), new ISODate().toString(), changeMessage);
		}
	}

	/** 
	  * Called when need to set status on a set of metadata records.
	  *
		* @param status The status to set.
		* @param metadataIds The set of metadata ids to set status on.
		* @param changeDate The date the status was changed.
		* @param changeMessage The message explaining why the status has changed.
		*/
	public Set<Integer> statusChange(String status, Set<Integer> metadataIds, String changeDate, String changeMessage) throws Exception {

		Set<Integer> unchanged = new HashSet<Integer>();

		//-- process the metadata records to set status
		for (Integer mid : metadataIds) {
			String currentStatus = dm.getCurrentStatus(dbms, mid);

			//--- if the status is already set to value of status then do nothing
			if (status.equals(currentStatus)) {
                if(context.isDebug())
                    context.debug("Metadata "+mid+" already has status "+mid);
				unchanged.add(mid);
			}

			if (status.equals(Params.Status.APPROVED)) {
				// setAllOperations(mid); - this is a short cut that could be enabled
			} else if (status.equals(Params.Status.DRAFT) || status.equals(Params.Status.REJECTED) || status.equals(Params.Status.RETIRED)) {
				unsetAllOperations(mid);
			}

			//--- set status, indexing is assumed to take place later
			dm.setStatusExt(context, dbms, mid, Integer.valueOf(status), changeDate, changeMessage);
		}


		//--- inform content reviewers if the status is submitted
    if (status.equals(Params.Status.SUBMITTED)) {
      informContentReviewers(metadataIds, changeDate, changeMessage);
    //--- inform owners if status is approved
    } else if (status.equals(Params.Status.APPROVED)) {
      informOwnersApprovedOrRejected(metadataIds, changeDate, changeMessage, true);
    //--- inform owners if status is rejected or retired
    } else if (status.equals(Params.Status.REJECTED) || status.equals(Params.Status.RETIRED)) {
      informOwnersApprovedOrRejected(metadataIds, changeDate, changeMessage, false);
    }	

		return unchanged;
	}

	//-------------------------------------------------------------------------
	
	//-------------------------------------------------------------------------

	/**
    * Set all operations on 'All' Group. Used when status changes from submitted to approved.
    *
    * @param mdId The metadata id to set privileges on
    */
  private void setAllOperations(int mdId) throws Exception {
    String allGroup = "1";
    dm.setOperation(context, dbms, mdId+"", allGroup, AccessManager.OPER_VIEW);
    dm.setOperation(context, dbms, mdId+"", allGroup, AccessManager.OPER_DOWNLOAD);
    dm.setOperation(context, dbms, mdId+"", allGroup, AccessManager.OPER_NOTIFY);
    dm.setOperation(context, dbms, mdId+"", allGroup, AccessManager.OPER_DYNAMIC);
    dm.setOperation(context, dbms, mdId+"", allGroup, AccessManager.OPER_FEATURED);
  }

  /**
    * Unset all operations on 'All' Group. Used when status changes from approved to something else. 
    *
    * @param mdId The metadata id to unset privileges on
    */
  private void unsetAllOperations(int mdId) throws Exception {
    String allGroup = "1";
    dm.unsetOperation(context, dbms, mdId+"", allGroup, AccessManager.OPER_VIEW);
    dm.unsetOperation(context, dbms, mdId+"", allGroup, AccessManager.OPER_DOWNLOAD);
    dm.unsetOperation(context, dbms, mdId+"", allGroup, AccessManager.OPER_NOTIFY);
    dm.unsetOperation(context, dbms, mdId+"", allGroup, AccessManager.OPER_DYNAMIC);
    dm.unsetOperation(context, dbms, mdId+"", allGroup, AccessManager.OPER_FEATURED);
  }
		
	/**
		* Inform content reviewers of metadata records in list that they need
		* to review the record.
		*
		* @param metadata The selected set of metadata records
		* @param changeDate The date that of the change in status
		* @param changeMessage Message supplied by the user that set the status
		*/
	private void informContentReviewers(Set<Integer> metadata, String changeDate, String changeMessage) throws Exception {

		//--- get content reviewers (sorted on content reviewer userid)
		Element contentRevs = am.getContentReviewers(dbms, metadata);
		List<Element> revList = contentRevs.getChildren();

		for (Element rev : revList) {
			// get reviewer ownergroup and set permissions so that content reviewer can review the record (may not have privs)
			String groupowner = rev.getChildText("groupowner");
			if (groupowner == null) {
				context.error("Content reviewer "+rev.getChildText("userid")+" doesn't have a groupowner! - they may not be able to review the record.");	
				continue; // skip
			}
			for (Integer mdId : metadata) {
				dm.setOperation(context, dbms, mdId+"", groupowner, AccessManager.OPER_VIEW);
				dm.setOperation(context, dbms, mdId+"", groupowner, AccessManager.OPER_DOWNLOAD);
				dm.setOperation(context, dbms, mdId+"", groupowner, AccessManager.OPER_NOTIFY);
				dm.setOperation(context, dbms, mdId+"", groupowner, AccessManager.OPER_EDITING);
			}
		}

		String subject = "Metadata records SUBMITTED by "+replyTo+" ("+replyToDescr+") on "+changeDate;
		processList(metadata, contentRevs, subject, Params.Status.SUBMITTED, changeDate, changeMessage);
	}

	/**
		* Inform owners of metadata records that the records have approved
		* or rejected.
		*
		* @param metadata The selected set of metadata records
		* @param changeDate The date that of the change in status
		* @param changeMessage Message supplied by the user that set the status
		*/
	private void informOwnersApprovedOrRejected(Set<Integer> metadata, String changeDate, String changeMessage, boolean approved) throws Exception {

		//--- get metadata owners (sorted on owner userid)
		Element owners = am.getOwners(dbms, metadata);

		String subject = "Metadata records APPROVED";
		String status = Params.Status.APPROVED;
		if (!approved) {
			subject = "Metadata records REJECTED";
			status = Params.Status.REJECTED;
		}
		subject += " by "+replyTo+" ("+replyToDescr+") on "+changeDate;

		processList(metadata, owners, subject, status, changeDate, changeMessage);

	}

	/**
		* Process the users and metadata records for emailing notices.
		*
		* @param users The selected set of users
		* @param subject Subject to be used for email notices
		* @param status The status being set
		* @param changeDate Datestamp of status change
		* @param changeMessage The message indicating why the status has changed
		*/
	private void processList(Set<Integer> metadata, Element users, String subject, String status, String changeDate, String changeMessage) throws Exception {

		List<Element> userList = users.getChildren();

		Set<String> emails = new HashSet<String>();

    // assemble uuid and title info for metadata records in the Set metadata
    String mdInfoForEmail = assembleMetadataLinks(metadata);

		for (Element user : userList) {
			emails.add(user.getChildText("email"));
		}

		for (String email : emails) {
			sendEmail(email, subject, status, changeDate, changeMessage, mdInfoForEmail);	
		}

	}

	/**
		* Send the email message about change of status on a group of metadata
		* records.
		*
		* @param sendTo The recipient email address
		* @param subject Subject to be used for email notices
		* @param status The status being set on the records
		* @param changeDate Datestamp of status change
		* @param changeMessage The message indicating why the status has changed
		*/
	private void sendEmail(String sendTo, String subject, String status, String changeDate, String changeMessage, String mdInfoForEmail) throws Exception {

    String statusText = getStatusText(status);

		StringBuffer message = new StringBuffer();
    message.append(changeMessage);
    message.append("\n\nRecords are available from the following URL:\n");
    message.append(buildMetadataSearchLink(status, changeDate)+"\n");
    message.append("\n");
    message.append("Status change requested is "+statusText+" by "+replyTo+" ("+session.getName()+" "+session.getSurname()+")\n");
    message.append("\n");
    message.append("Individual records are: \n");
    message.append(mdInfoForEmail); 
    message.append("\n");

		if (!emailNotes) {
			context.info("Would send email \nTo: "+sendTo+"\nSubject: "+subject+"\n Message:\n"+message.toString());
		} else {
			MailSender sender = new MailSender(context);
			sender.sendWithReplyTo(host, Integer.parseInt(port), from, fromDescr, sendTo, null, replyTo, replyToDescr, subject, message.toString());
		}
	}

	/**
		* Build search link to metadata that has had a change of status.
		*
		* @param status The status of the metadata 
		* @param changeDate The date the status has been set on the metadata 
		* @return string Search link to metadata
		*/
	private String buildMetadataSearchLink(String status, String changeDate) {
		String suffix = "&_status="+status+"&_statusChangeDate="+changeDate;
		// FIXME : hard coded link to search URLs
		if (html5ui) {
		  return siteUrl+"/search#fast=index&from=1&to=50"+suffix;
		} else {
		  return siteUrl+"/main.search?"+suffix;
		}
	}

  private String assembleMetadataLinks(Set<Integer> metadata) {
    StringBuffer buffer = new StringBuffer();
    for (int id : metadata) {
       try {
         Element md = dm.getMetadata(context, dbms, id+"", false, false, false);
         Element summary = dm.extractSummary(md);
         String title = summary.getChildText("title");
         Element info = md.getChild(Edit.RootChild.INFO, Edit.NAMESPACE);
         String uuid = info.getChildText("uuid");
         buffer.append(siteUrl+"/search?uuid="+uuid+"  Title: "+title+"\n");
       } catch (Exception e) {
				 context.error("Error retrieving metadata id "+id+"; will skip");
         e.printStackTrace();         
       }
    }
    return buffer.toString();
  }

  private String getStatusText(String status) {
   
    if      (status.equals(Params.Status.SUBMITTED)) return "SUBMITTED";
    else if (status.equals(Params.Status.APPROVED))  return "APPROVED";
    else if (status.equals(Params.Status.REJECTED))  return "REJECTED";
    else if (status.equals(Params.Status.RETIRED))   return "RETIRED";
    else if (status.equals(Params.Status.DRAFT))     return "DRAFT";
    else                                             return "UNKNOWN";
  }

	public void setUseHtml5ui(boolean html5ui) {
		DefaultStatusActions.html5ui = html5ui;
	}
        
}
