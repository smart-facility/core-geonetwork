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

import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.harvest.harvester.RecordInfo;
import org.fao.geonet.kernel.search.spatial.Pair;
import org.fao.geonet.util.ISODate;

import jeeves.interfaces.Logger;
import jeeves.utils.Log;
import jeeves.utils.Xml;

import com.hof.mi.web.service.AdministrationPerson;
import com.hof.mi.web.service.AdministrationReport;
import com.hof.mi.web.service.AdministrationServiceClient;
import com.hof.mi.web.service.AdministrationServiceResponse;
import com.hof.mi.web.service.AdministrationServiceRequest;
import com.hof.mi.web.service.AdministrationServiceService;
import com.hof.mi.web.service.AdministrationServiceServiceLocator;
import com.hof.mi.web.service.AdministrationServiceSoapBindingStub;
import com.hof.mi.web.service.ReportSchema;
import com.hof.mi.web.service.ReportServiceServiceLocator;
import com.hof.mi.web.service.ReportServiceSoapBindingStub;
import com.hof.mi.web.service.ReportServiceClient;
import com.hof.mi.web.service.WebserviceException;
import com.hof.mi.web.service.i4Report;

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
		this.as = new AdministrationServiceServiceLocator(params.hostname, params.port, baseUrl+"AdministrationService", false);
    this.assbs = (AdministrationServiceSoapBindingStub) this.as.getAdministrationService();

		AdministrationServiceRequest asr = setupAdminServRequest();

		AdministrationServiceResponse as = null;
		AdministrationPerson person = new AdministrationPerson();
		person.setUserId("simon.pigot@csiro.au");
		asr.setFunction("GETALLUSERREPORTS");
		asr.setPerson(person);

		as = this.assbs.remoteAdministrationCall(asr);
		if ("SUCCESS".equals(as.getStatusCode()) ) {
			Log.debug(Geonet.HARVESTER, "Success");
		} else {
			Log.debug(Geonet.HARVESTER, "Failure Code: " + as.getErrorCode());
			throw new Exception("Failed to connect to yellowfin: "+as.getErrorCode());
		}	

		// get reports - filter with search expression?
		reports = as.getReports();
		Log.error(Geonet.HARVESTER, "Found "+reports.length+" yellowfin reports accessible by user "+params.username);
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

		ReportServiceClient rsc = new ReportServiceClient(params.hostname, params.port, params.username, params.password, baseUrl+"ReportService");

		Set<RecordInfo> results = new HashSet<RecordInfo>();

		this.currentReports = new HashMap<String, Pair<AdministrationReport,i4Report>>();
		this.personCache = new HashMap<Integer, AdministrationPerson>();

		// process each report, extracting report id and modification date to
		// create a RecordInfo object to pass back to GeoNetwork and load
		// the report from yellowfin, storing the i4Report object mapped by
		// report UUID
		for (int i = 0;i < reports.length;i++) {
			AdministrationReport ar = reports[i];

			Log.error(Geonet.HARVESTER,"Adding report "+ar.getReportUUID());
			RecordInfo ri = new RecordInfo(ar.getReportUUID(), new ISODate(ar.getLastModifiedDate().getTime()).toString());	
			results.add(ri);

			// get report from the report service client
			i4Report rep = rsc.loadReportForUser(ar.getExecutionObject(), params.username, params.password, null);
			this.currentReports.put(ar.getReportUUID(), Pair.read(ar,rep));
		}

		return results;
	}

	//---------------------------------------------------------------------------

	public Element getRecord(String uuid) {
		i4Report rep = this.currentReports.get(uuid).two();
		AdministrationReport report = this.currentReports.get(uuid).one();
		if (report == null) return null;

		// Get the administration report into XML format (again)
		Element reportXml = streamObject(report);
		System.out.println(Xml.getString(reportXml));

		// Get the i4report into XML format (again)
		Element bigReportXml = streamObject(new SmallReport(rep));
		addColumnsToReportXml(rep, bigReportXml);
		System.out.println(Xml.getString(bigReportXml));
		
		// Get last modifier id and then use that to get the user details of
		// the user that modified it
		AdministrationPerson person = getPersonById(report.getLastModifierId());
		if (person != null) {
			Element personXml = streamObject(person);
			System.out.println(Xml.getString(personXml));
		} else {
			Log.error(Geonet.HARVESTER, "Yellowfin modifier by ip "+report.getLastModifierId()+" doesn't exist");
		}

		// just create a simple metadata record - more to be done here
		Element result = new Element("MD_Metadata", Geonet.Namespaces.GMD);
		return result;
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
				Log.debug(Geonet.HARVESTER, "Success");
				AdministrationPerson ap = as.getPerson();
				if (ap != null) personCache.put(id, ap);
				return ap;
			} else {
				Log.debug(Geonet.HARVESTER, "Failure Code: " + as.getErrorCode());
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

		//create the container element which the serialized object will go into
		Element container = new Element("container");
		//marshall the onject into the container
		xStream.marshal(obj, new JDomWriter(container));
		return (Element) container.getChild(alias).detach();
	}

	//---------------------------------------------------------------------------

	private Element addColumnsToReportXml(i4Report rep, Element repXml) {
		ReportSchema[] cols = rep.getColumns();
		Element columns = new Element("columns");
		for (int i = 0;i < cols.length;i++) {
			columns
		.addContent(new Element("name").setText(cols[i].getColumnName()))
		.addContent(new Element("datatype").setText(cols[i].getDataType()))
		.addContent(new Element("displayName").setText(cols[i].getDisplayName()));
		}
		repXml.addContent(columns);
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
	private AdministrationServiceClient asc;
	private ReportServiceClient rsc;
	private AdministrationPerson person;
	private Map<String, Pair<AdministrationReport, i4Report>> currentReports;
	private String baseUrl = "/services/";
	private AdministrationReport[] reports;
	private AdministrationServiceService as; 
  private AdministrationServiceSoapBindingStub assbs;
	private Map<Integer,AdministrationPerson> personCache;
}

//=============================================================================


