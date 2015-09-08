//=============================================================================
//===	Copyright (C) 2001-2013 Food and Agriculture Organization of the
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
//=============================================================================

package org.fao.geonet.kernel.harvest.harvester.yellowfin;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.JDomWriter;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.harvest.harvester.RecordInfo;
import org.fao.geonet.kernel.search.spatial.Pair;
import org.fao.geonet.util.ISODate;

import jeeves.interfaces.Logger;
import jeeves.utils.Xml;

import com.hof.mi.web.service.AdministrationPerson;
import com.hof.mi.web.service.AdministrationReport;
import com.hof.mi.web.service.AdministrationServiceResponse;
import com.hof.mi.web.service.AdministrationServiceRequest;
import com.hof.mi.web.service.AdministrationServiceService;
import com.hof.mi.web.service.AdministrationServiceServiceLocator;
import com.hof.mi.web.service.AdministrationServiceSoapBindingStub;
import com.hof.mi.web.service.GISLayer;
import com.hof.mi.web.service.GISMap;
import com.hof.mi.web.service.GISShape;
import com.hof.mi.web.service.RelatedReport;
import com.hof.mi.web.service.RelatedReports;
import com.hof.mi.web.service.ReportSchema;
import com.hof.mi.web.service.ReportServiceClient;
import com.hof.mi.web.service.WebserviceException;
import com.hof.mi.web.service.i4Report;

import com.vividsolutions.jts.geom.Envelope;

import org.jdom.Element;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//=============================================================================

class YwfsRequest
{
	//--------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//--------------------------------------------------------------------------

	public YwfsRequest(YellowfinParams params, Logger log) throws Exception {
		this.params = params;
		if (params.secure) this.protocol = "https://";
		this.log = log;
		this.as = new AdministrationServiceServiceLocator(params.hostname, params.port, baseUrl+"AdministrationService", params.secure);
    this.assbs = (AdministrationServiceSoapBindingStub) this.as.getAdministrationService();

		AdministrationServiceRequest asr = setupAdminServRequest();

		AdministrationServiceResponse as = null;
		AdministrationPerson person = new AdministrationPerson();
		person.setUserId(params.username);
		asr.setFunction("GETALLUSERREPORTS");
		asr.setPerson(person);

		as = this.assbs.remoteAdministrationCall(asr);
		if ("SUCCESS".equals(as.getStatusCode()) ) {
			log.debug( "Success");
		} else {
			throw new Exception("Couldn't get all user reports from yellowfin: "+as.getErrorCode());
		}	

		// get reports - filter with search expression?
		reports = as.getReports();
		log.info( "Found "+reports.length+" yellowfin reports accessible by user "+params.username);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setSearchExpression(String searchExpression) {
		this.searchExpression = searchExpression;
	}

	//---------------------------------------------------------------------------

	public String getSearchExpression() {
		return this.searchExpression;
	}

	//---------------------------------------------------------------------------

	public Set<RecordInfo> execute() throws Exception {

		ReportServiceClient rsc = new ReportServiceClient(params.hostname, params.port, params.username, params.password, baseUrl+"ReportService", params.secure, false, "geonetworkHarvester", true);

		Set<RecordInfo> results = new HashSet<RecordInfo>();

		this.currentReports = new HashMap<String, Pair<AdministrationReport,i4Report>>();
		this.reportsById = new HashMap<Integer, String>();
		this.personCache = new HashMap<Integer, AdministrationPerson>();
		this.personNameCache = new HashMap<String, AdministrationPerson>();

		// process each report, extracting report id and modification date to
		// create a RecordInfo object to pass back to GeoNetwork and load
		// the report from yellowfin, storing the i4Report object mapped by
		// report UUID
		for (int i = 0;i < reports.length;i++) {
			AdministrationReport ar = reports[i];

			log.info("Adding report "+ar.getReportUUID());
			RecordInfo ri = new RecordInfo(ar.getReportUUID(), new ISODate(ar.getLastModifiedDate().getTime()).toString());	
			results.add(ri);

			// get report from the report service client
			i4Report rep = rsc.LoadReport(ar.getReportId());
			this.currentReports.put(ar.getReportUUID(), Pair.read(ar,rep));
			this.reportsById.put(ar.getReportId(), ar.getReportUUID());
		}

		return results;
	}

	//---------------------------------------------------------------------------

	public Element getRecord(String uuid) {
		i4Report rep = this.currentReports.get(uuid).two();
		AdministrationReport report = this.currentReports.get(uuid).one();
		if (report == null) return null;

		Element root = new Element("root");
		root.setAttribute("uuid", uuid);

		// Get the administration report into XML format (again)
		Element reportXml = streamObject(report);
		reportXml.addContent(
			new Element("reportURL").setText(this.protocol+params.hostname+":"+params.port+"/RunReport.i4?reportUUID="+uuid+"&primaryOrg=1&clientOrg=1"));
		reportXml.addContent(
			new Element("dateStamp").setText(new ISODate(new Date().getTime()).toString()));
		root.addContent(reportXml);

		// Get the i4report into XML format (again)
		Element bigReportXml = streamObject(new SmallReport(rep));
		addColumnsToReportXml(rep, bigReportXml);
		addShapesToReportXml(rep, bigReportXml);
		addRelatedReportsToReportXml(rep, bigReportXml);
		root.addContent(bigReportXml);
		
		// Get last modifier id and then use that to get the user details of
		// the user that modified it
		AdministrationPerson person = getPersonById(report.getLastModifierId());
		if (person != null) {
			Element personXml = streamObject(person);
			personXml.setAttribute("role","processor");
			root.addContent(personXml);
		} else {
			log.warning( "Yellowfin report modifier by ip "+report.getLastModifierId()+" doesn't exist");
		}

		// Get author name and then use that to get the user details of
		// the user that authored the report 
		person = getPersonByName(rep.getAuthor());
		if (person != null) {
			Element personXml = streamObject(person);
			personXml.setAttribute("role","author");
			root.addContent(personXml);
		} else {
			log.warning( "Yellowfin report author "+rep.getAuthor()+" doesn't exist");
		}
		log.debug("Prepared for fragments: "+Xml.getString(root));

		return root;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private AdministrationServiceRequest setupAdminServRequest() {
		AdministrationServiceRequest asr = new AdministrationServiceRequest();
		asr.setLoginId(params.username);
		asr.setPassword(params.password);
		asr.setOrgId(new Integer(1));
		return asr;
	}

	//---------------------------------------------------------------------------

	private AdministrationPerson getPersonByName(String userFirstNameLastName) {
		// persons who appear as authors don't necessarily appear as modifiers
		// so we need to do our own cache
		if (personNameCache.get(userFirstNameLastName) != null) return personNameCache.get(userFirstNameLastName);

		AdministrationPerson person = new AdministrationPerson();

		// split the userFirstNameLastName into two parts by space
		String[] names = userFirstNameLastName.split(" ");
		if (names.length != 2) {
			log.warning( "Metadata author field ("+userFirstNameLastName+") didn't have expected format 'firstname lastname'");
			person.setUserId(userFirstNameLastName);
		 	return person;	
		}
		
		// now search using search strings
		AdministrationServiceRequest asr = setupAdminServRequest();
		asr.setParameters(names);
		asr.setFunction("GETUSERSFROMSEARCH");

		AdministrationServiceResponse as = null;
		try {
			as = this.assbs.remoteAdministrationCall(asr);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ("SUCCESS".equals(as.getStatusCode()) ) {
				log.debug( "Success");
				AdministrationPerson[] ap = as.getPeople();
				if (ap != null && ap.length > 0) {
					personNameCache.put(userFirstNameLastName, ap[0]);
					return ap[0];
				} else {
					log.warning( "Couldn't find yellowfin user with names: " +userFirstNameLastName);
					return null;
				}
			} else {
				log.error( "Looking up yellowfin user "+userFirstNameLastName+" failed: " + as.getErrorCode());
				return null;
			}	
		}

	}

	//---------------------------------------------------------------------------

	private AdministrationPerson getPersonById(Integer id) {
		if (personCache.get(id) != null) return personCache.get(id);

		AdministrationServiceRequest asr = setupAdminServRequest();

		AdministrationPerson person = new AdministrationPerson();
		person.setIpId(id);
		asr.setPerson(person);
		asr.setFunction("GETUSERBYIP");

		AdministrationServiceResponse as = null;
		try {
			as = this.assbs.remoteAdministrationCall(asr);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ("SUCCESS".equals(as.getStatusCode()) ) {
				log.debug( "Success");
				AdministrationPerson ap = as.getPerson();
				if (ap != null) personCache.put(id, ap);
				return ap;
			} else {
				log.error( "Yellowfin user by ip lookup on "+id+" failed: " + as.getErrorCode());
				return null;
			}	
		}

	}

	//---------------------------------------------------------------------------

	private Element streamObject(Object obj) {
		XStream xStream = new XStream();
		//create an alias for class to give it the simple name 
		String alias = obj.getClass().getSimpleName();
		//add the alias for the User class
		xStream.alias(alias, obj.getClass());
		if (obj instanceof AdministrationReport) {
			YwfsDateConverter dc = new YwfsDateConverter();
			xStream.registerLocalConverter(obj.getClass(), "PublishDate", dc);
			xStream.registerLocalConverter(obj.getClass(), "LastModifiedDate", dc);
		}

		//create the container element which the serialized object will go into
		Element container = new Element("container");
		//marshall the onject into the container
		xStream.marshal(obj, new JDomWriter(container));
		return (Element) container.getChild(alias).detach();
	}

	//---------------------------------------------------------------------------

	public class YwfsDateConverter extends AbstractSingleValueConverter {
    public boolean canConvert(Class clazz) {
      return clazz.equals(Date.class);
     }
     public Object fromString(String str) { // don't care here
       return new Date(); // don't care
     }
		 @Override
     public String toString(Object obj) {
			 if (obj == null) return null;
			 Date theDate = (Date)obj;
			 return new ISODate(theDate.getTime()).toString();
     }
	}

	//---------------------------------------------------------------------------

	private Element addRelatedReportsToReportXml(i4Report rep, Element repXml) {
		Element relatedElem = new Element("relatedReports");
		RelatedReports relReports = rep.getRelatedReports();
		if (relReports != null) {
			RelatedReport[] relatedReports = relReports.getRelatedReports();
			for (int i = 0;i < relatedReports.length;i++) {
				RelatedReport report = relatedReports[i];
				String relRepUUID = reportsById.get(report.getReportId());
				if (relRepUUID != null) { // skip any that aren't available to us
					relatedElem.addContent(
						new Element("report").setText(relRepUUID));
				} else {
					log.error("Skipping report "+report.getReportId());
				}
			}
		}
		repXml.addContent(relatedElem);
		return repXml;
	}

	//---------------------------------------------------------------------------

	private Element addShapesToReportXml(i4Report rep, Element repXml) {
		GISMap[] maps = rep.getGisMap();
		Element bnd = new Element("bounds");
		Element lay = new Element("layers");
		Envelope boundsEnv = new Envelope();
		if (maps != null) {
			for (int i = 0;i < maps.length;i++) {
				GISMap map = maps[i];
				GISLayer[] layers = map.getLayers();
				if (layers != null) {
					for (int j = 0;j < layers.length;j++) {
						// displayName property is protected in yfws-6.3
						//lay.addContent(new Element("layer").setText(layers[j].getDisplayName()));
						lay.addContent(new Element("layer").setText(layers[j].displayName));
					}
				}
				GISShape[] shapes = map.getShapes();
				if (shapes != null) {
					for (int k = 0;k < shapes.length;k++) {
						GISShape s = shapes[k];
						Envelope shapeEnv = new Envelope(
													s.getBoundsLeft(),
													s.getBoundsLeft()+s.getBoundsWidth(),
													s.getBoundsTop()-s.getBoundsHeight(),
													s.getBoundsTop());
						boundsEnv.expandToInclude(shapeEnv);
					}
					if (!boundsEnv.isNull()) {
						bnd
						.addContent(new Element("minX").setText(boundsEnv.getMinX()+""))
						.addContent(new Element("minY").setText(boundsEnv.getMinY()+""))
						.addContent(new Element("maxX").setText(boundsEnv.getMaxX()+""))
						.addContent(new Element("maxY").setText(boundsEnv.getMaxY()+""));
					}
				}
			}
		}
		repXml.addContent(bnd);
		repXml.addContent(lay);
		return repXml;
	}

	//---------------------------------------------------------------------------

	private Element addColumnsToReportXml(i4Report rep, Element repXml) {
		ReportSchema[] cols = rep.getColumns();
		Element columns = new Element("columns");
		for (int i = 0;i < cols.length;i++) {
			Element column = new Element("column");
			column
		.addContent(new Element("name").setText(cols[i].getColumnName()))
		.addContent(new Element("datatype").setText(cols[i].getDataType()))
		.addContent(new Element("displayName").setText(cols[i].getDisplayName()));
			columns.addContent(column);
		}
		repXml.addContent(columns);

		cols = rep.getFilterSchema();
		Element filterColumns = new Element("filterColumns");
		for (int i = 0;i < cols.length;i++) {
			Element column = new Element("column");
			column
		.addContent(new Element("name").setText(cols[i].getColumnName()))
		.addContent(new Element("datatype").setText(cols[i].getDataType()))
		.addContent(new Element("displayName").setText(cols[i].getDisplayName()));
			filterColumns.addContent(column);
		}
		repXml.addContent(filterColumns);
		return repXml;
	}


	//---------------------------------------------------------------------------
	//---
	//--- Variables
	//---
	//---------------------------------------------------------------------------
	private Logger         log;
	private YellowfinParams params;
	private String searchExpression;
	private ReportServiceClient rsc;
	private AdministrationPerson person;
	private Map<String, Pair<AdministrationReport, i4Report>> currentReports;
	private Map<Integer, String> reportsById;
	private String baseUrl = "/services/";
	private AdministrationReport[] reports;
	private AdministrationServiceService as;
  private AdministrationServiceSoapBindingStub assbs;
	private Map<Integer,AdministrationPerson> personCache;
	private Map<String,AdministrationPerson> personNameCache;
	private String protocol = "http://";
}

//=============================================================================


