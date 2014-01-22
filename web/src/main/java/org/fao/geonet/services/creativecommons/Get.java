//=============================================================================
//===	Copyright (C) 2001-2005 Food and Agriculture Organization of the
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
//===	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//===
//===	Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
//===	Rome - Italy. email: GeoNetwork@fao.org
//==============================================================================

package org.fao.geonet.services.creativecommons;

import java.io.File;
import java.net.URL;
import java.util.List;
import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;
import jeeves.utils.Xml;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.constants.Params;
import org.fao.geonet.kernel.AccessManager;
import org.fao.geonet.services.metadata.Update;
import org.fao.geonet.lib.ResourceLib;
import org.jdom.Element;

//=============================================================================

/** Handles the get operation
  */

public class Get implements Service
{
	private Element config;
	private Update  update = new Update();

	//----------------------------------------------------------------------------
	//---
	//--- Init
	//---
	//----------------------------------------------------------------------------

	public void init(String appPath, ServiceConfig params) throws Exception
	{
		update.init(appPath, params);
	}

	//----------------------------------------------------------------------------
	//---
	//--- Service
	//---
	//----------------------------------------------------------------------------

	public Element exec(Element params, ServiceContext context) throws Exception
	{

		if (saveEditData(params))
      //--- data is not saved if someone else has changed the metadata
      update.exec(params, context);

		String id = Util.getParam(params, Params.ID);
		String version = Util.getParam(params, Params.VERSION);
		String name	= Util.getParam(params, "name");
		String licenseurl	= Util.getParam(params, "licenseurl");
		String type	= Util.getParam(params, "type");


		Element results = new Element("creativecommons");
		results.setAttribute("jurisdiction",name);
		results.setAttribute("id",id);
		results.setAttribute("licenseurl",licenseurl);
		results.setAttribute("version",version);
		results.setAttribute("type",type);

		// Get the Creative Commons License for the jurisdiction specified. 
		// This info is supplied by calling the CC Rest interface.
		String[] parts  = name.split("/");
		String ccLicenseUrl = Geonet.CC_API_REST_URL + "?jurisdiction=" + parts[parts.length-1] + "&locale=" + context.getLanguage();
		Element license = null;
		try {
			license = Xml.loadFile(new URL(ccLicenseUrl));
		} catch (Exception e) {
			context.error("Cannot load license from " + ccLicenseUrl + e); 
		}

		if (license != null) {
			results.addContent(license);
		} else {
			// If we don't get anything from the CC Rest service we pass on 
			// on the params directly to metadata-creativecommons.xsl which gets the 
			// CC licenses from canned xml files
			context.info("CreativeCommons.org REST interface is not being used - CC data is static");
		}

		return results;
	}

	//--------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//--------------------------------------------------------------------------

	private boolean saveEditData(Element params)
  {
    List list = params.getChildren();

    for(int i=0; i<list.size(); i++)
    {
      Element el = (Element) list.get(i);

      if (el.getName().startsWith("_"))
        return true;
    }

    return false;
  }
}
//=============================================================================

