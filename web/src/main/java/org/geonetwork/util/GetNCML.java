//==============================================================================
//===
//===   GetNCML       
//===
//==============================================================================
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

package org.fao.geonet.util;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.transform.JDOMSource;

import jeeves.utils.Xml;

import ucar.nc2.NetcdfFile;
import ucar.nc2.NCdumpW;
import ucar.nc2.Variable;

import java.io.File;
import java.io.FilenameFilter;
import java.io.StringWriter;

import javax.xml.transform.Source;

//==============================================================================

public class GetNCML
{

/**
 * Open netcdf file on path and return an ncml description
 *
 * @author sppigot
 */
	public static Source getNCML(String path) {
		Source srcXml = null;

		// sometimes we might be looking at a directory of files, if so then 
		// open and get the first one
		File fPath = new File(path);
		if (fPath.isDirectory()) {
			// create new filename filter
      FilenameFilter fileNameFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
               if (name.endsWith(".nc")) {
								return true;
               } else {
               	return false;
            	}
						}
         };
			String[] paths = fPath.list(fileNameFilter);	
			if (paths != null && paths.length > 0) { 
				path = path + File.separator + paths[0]; // select the first one, as they should be the same
			}
		}

		// check if it is a netcdf file (ends in .nc)
		if (path.endsWith(".nc")) {
			try {
				NetcdfFile ncdf = NetcdfFile.open(path);
				StringWriter sw = new StringWriter();
				NCdumpW.writeNcML(ncdf, sw, false, null);
				Element test = Xml.loadString(sw.toString(), false);
				srcXml   = new JDOMSource(new Document((Element)test.detach()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// return what we found: either an ncml description or nothing (null)
		return srcXml;
	}
}
//==============================================================================




