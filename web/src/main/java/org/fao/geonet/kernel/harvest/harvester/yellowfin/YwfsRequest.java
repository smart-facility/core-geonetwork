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

import org.fao.geonet.kernel.harvest.harvester.RecordInfo;
import org.fao.geonet.util.ISODate;

import jeeves.interfaces.Logger;

import com.hof.mi.web.service.AdministrationPerson;
import com.hof.mi.web.service.AdministrationReport;
import com.hof.mi.web.service.AdministrationServiceClient;
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
		this.log = log;

		// Initiate session with yellowfin
		try {
			this.asc = new AdministrationServiceClient(params.hostname, params.port, params.username, params.password, baseUrl+"AdminstrationService");
			this.rsc = new ReportServiceClient(params.hostname, params.port, params.username, params.password, baseUrl+"ReportService");
		} catch (WebserviceException we) {
			we.printStackTrace();
			throw new Exception(we.getMessage());
		}

		this.person = asc.getUser(params.username);
		this.currentReports = new HashMap<String, i4Report>();
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

	public Set<RecordInfo> execute() {
		Set<RecordInfo> results = new HashSet<RecordInfo>();

		// execute a get reports with search expression
		AdministrationReport[] reports = asc.listAllReportsFromRegex(searchExpression,	person);

		this.currentReports = new HashMap<String, i4Report>();

		// process each report, extracting report id and modification date to
		// create a RecordInfo object to pass back to GeoNetwork
		for (int i = 0;i < reports.length;i++) {
			AdministrationReport ar = reports[i];

			i4Report report = rsc.loadReportForUser(ar.getExecutionObject(), person.getUserId(), person.getPassword(), null);

			String urn = "urn:smart-uow:yellowfin:"+report.getReportId();
			RecordInfo ri = new RecordInfo(urn, new ISODate(report.getLastModifiedDate()).toString());	
			results.add(ri);
			currentReports.put(urn, report);
		}

		return results;
	}

	//---------------------------------------------------------------------------

	public Element getRecord(String uuid) {
		i4Report report = currentReports.get(uuid);
		if (report == null) return null;

		Element result = new Element("result");
		return result;
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
	private Map<String, i4Report> currentReports;
	private String baseUrl = "/yellowfin/services/";
}

//=============================================================================


