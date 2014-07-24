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
//==============================================================================

package org.fao.geonet.kernel.harvest.harvester.yellowfin;

import jeeves.exceptions.BadParameterEx;
import jeeves.exceptions.OperationAbortedEx;
import jeeves.interfaces.Logger;
import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Xml;
import jeeves.xlink.Processor;

import org.apache.commons.lang.StringUtils;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.kernel.SchemaManager;
import org.fao.geonet.kernel.harvest.harvester.fragment.FragmentHarvester;
import org.fao.geonet.kernel.harvest.harvester.fragment.FragmentHarvester.FragmentParams;
import org.fao.geonet.kernel.harvest.harvester.fragment.FragmentHarvester.HarvestSummary;
import org.fao.geonet.kernel.harvest.harvester.RecordInfo;
import org.fao.geonet.kernel.harvest.harvester.UUIDMapper;
import org.fao.geonet.lib.Lib;
import org.fao.geonet.util.ISODate;
import org.jdom.Element;

import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


//=============================================================================

class Harvester
{
	//--------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//--------------------------------------------------------------------------

	public Harvester(Logger log, ServiceContext context, Dbms dbms, YellowfinParams params)
	{
		this.log    = log;
		this.context= context;
		this.dbms   = dbms;
		this.params = params;

		//--- set up a URL from the yellowfin source 
		this.harvestUrl = "http://"+params.hostname+":"+params.port;

		GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
		dataMan = gc.getDataManager();
		schemaMan = gc.getSchemamanager();
		metadataGetService = "local://xml.metadata.get";
		result  = new YellowfinResult();
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public YellowfinResult harvest() throws Exception
	{

		//--- perform all searches

		YwfsRequest request = new YwfsRequest(params, log);

		Set<RecordInfo> records = new HashSet<RecordInfo>();

		for(Search s : params.getSearches())
			records.addAll(search(request, s));

		if (params.isSearchEmpty())
			records.addAll(search(request, Search.createEmptySearch()));

		log.info("Total records processed in all searches :"+ records.size());

		localUuids = new UUIDMapper(dbms, params.uuid);
		updatedMetadata = new HashSet<String>();

		//--- harvest metadata and subtemplates from fragments using generic fragment harvester
    FragmentHarvester fragmentHarvester = new FragmentHarvester(log, context, dbms, getFragmentHarvesterParams());

		for (RecordInfo ri : records) {
			if (log.isDebugEnabled()) log.debug("Getting record (uuid:"+ ri.uuid +")");
      Element response = null;
      try {
        response = request.getRecord(ri.uuid);
      } catch (Exception e) {
        e.printStackTrace();
        log.error("Getting record from yellowfin raised exception: "+e.getMessage());
        throw new Exception(e);
      }

      // now translate the temporary format returned from yf to fragments
      String stylesheetDirectory = schemaMan.getSchemaDir(params.outputSchema) + Geonet.Path.YELLOWFIN_STYLESHEETS;
      if (!params.stylesheet.trim().equals("")) {
        response = Xml.transform(response, stylesheetDirectory + "/" + params.stylesheet, ssParams);
      }

      if(log.isDebugEnabled()) log.debug("Got:\n"+Xml.getString(response));

      // now do the fragment harvester stuff
			harvest(response, fragmentHarvester);
		}

		deleteOrphanedMetadata();

    return result;	

	}

	//---------------------------------------------------------------------------

	/**
	 * Does Yellowfin search request.
	 */
	private Set<RecordInfo> search(YwfsRequest request, Search s) throws Exception
	{
		request.setSearchExpression(s.freeText);
		return doSearch(request);
	}

	//---------------------------------------------------------------------------

	private Set<RecordInfo> doSearch(YwfsRequest request) throws Exception
	{
		try {
			log.info("Searching on : "+ params.name);
			Set<RecordInfo> response = request.execute();
      if (log.isDebugEnabled()) {
      	log.debug("Sent request "+request.getSearchExpression());
				log.debug("Number of results returned: "+response.size());
			}
			return response;
		} catch(Exception e) {
			log.warning("Raised exception when searching : "+ e);
			e.printStackTrace();
			throw new OperationAbortedEx("Raised exception when searching: " + e.getMessage(), e);
		}
	}

	
	//---------------------------------------------------------------------------

	/** 
   * Harvest fragments from the element passed
   */
  private void harvest(Element xml, FragmentHarvester fragmentHarvester) throws Exception {

    HarvestSummary fragmentResult = fragmentHarvester.harvest(xml, this.harvestUrl);

    updatedMetadata.addAll(fragmentResult.updatedMetadata);

    result.fragmentsReturned += fragmentResult.fragmentsReturned;
    result.fragmentsUnknownSchema += fragmentResult.fragmentsUnknownSchema;
    result.subtemplatesAdded += fragmentResult.fragmentsAdded;
    result.fragmentsMatched += fragmentResult.fragmentsMatched;
    result.recordsBuilt += fragmentResult.recordsBuilt;
    result.recordsUpdated += fragmentResult.recordsUpdated;
    result.subtemplatesUpdated += fragmentResult.fragmentsUpdated;

    result.total = result.subtemplatesAdded + result.recordsBuilt;
  }

	//---------------------------------------------------------------------------

	/** 
   * Remove old metadata and subtemplates and uncache any subtemplates
   * that are left over after the update.
   */
  public void deleteOrphanedMetadata() throws Exception {

     if(log.isDebugEnabled()) log.debug("  - Removing orphaned metadata records and fragments after update");

    for (String uuid : localUuids.getUUIDs()) {
      String isTemplate = localUuids.getTemplate(uuid);
      if (isTemplate.equals("s")) {
          Processor.uncacheXLinkUri(metadataGetService+"?uuid=" + uuid);
      }

      if (!updatedMetadata.contains(uuid)) {
        String id = localUuids.getID(uuid);
				if (id == null) id = dataMan.getMetadataId(dbms, uuid);
        dataMan.deleteMetadata(context, dbms, id);

        if (isTemplate.equals("s")) {
          result.subtemplatesRemoved ++;
        } else {
          result.recordsRemoved ++;
        }
      }
    }

    if (result.subtemplatesRemoved + result.recordsRemoved > 0)  {
      dbms.commit();
    }
  }

	//---------------------------------------------------------------------------

	/** 
   * Get generic fragment harvesting parameters from metadata fragment 
   * harvesting parameters.
   *   
   */

  private FragmentParams getFragmentHarvesterParams() {
    FragmentParams fragmentParams = new FragmentHarvester.FragmentParams();
    fragmentParams.categories = params.getCategories();
    fragmentParams.createSubtemplates = false; // disabled for this harvester
    fragmentParams.outputSchema = params.outputSchema;
    fragmentParams.isoCategory = null; // disabled for this harvester
    fragmentParams.privileges = params.getPrivileges();
    fragmentParams.templateId = params.templateId;
    fragmentParams.url = ""; 
    fragmentParams.uuid = params.uuid;
    fragmentParams.owner = params.ownerId;
    return fragmentParams;
  }

	//---------------------------------------------------------------------------
	//---
	//--- Variables
	//---
	//---------------------------------------------------------------------------
	private Logger         log;
	private Dbms           dbms;
	private YellowfinParams params;
	private ServiceContext context;
	private SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
	private String harvestUrl;
	private DataManager    dataMan;
	private SchemaManager  schemaMan;
	private UUIDMapper     localUuids;
	private YellowfinResult      result;
	private String metadataGetService;
	private Map<String,String> ssParams = new HashMap<String,String>();
	private Set<String> updatedMetadata = new HashSet<String>();
}

//=============================================================================


