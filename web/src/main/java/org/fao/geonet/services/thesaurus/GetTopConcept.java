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

package org.fao.geonet.services.thesaurus;

import java.util.List;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.KeywordBean;
import org.fao.geonet.kernel.ThesaurusManager;
import org.fao.geonet.kernel.search.KeywordsSearcher;
import org.fao.geonet.languages.IsoLanguagesMapper;
import org.jdom.Element;

/**
 * Search the thesaurus for all concepts listed in the concept schema as 
 * top concepts (ie skos:hasTopConcept). Return a confected concept uri and 
 * preferred label with top concepts as narrower concepts.
 * 
 * @author sppigot
 */
public class GetTopConcept implements Service {
    public void init(String appPath, ServiceConfig params) throws Exception {
    }

    public Element exec(Element params, ServiceContext context)
            throws Exception {
        String sThesaurusName = Util.getParam(params, "thesaurus");
        String lang = Util.getParam(params, "lang", context.getLanguage());
        String langForThesaurus = IsoLanguagesMapper.getInstance()
                .iso639_2_to_iso639_1(lang);

        KeywordsSearcher searcher = null;

        GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
        ThesaurusManager thesaurusMan = gc.getThesaurusManager();
        
        
        
       	// perform the search for the top concepts of the concept scheme
        searcher = new KeywordsSearcher(thesaurusMan);
        List<KeywordBean> kbList = searcher.searchTopConcepts(sThesaurusName, langForThesaurus);
			
				KeywordBean topConcept = new KeywordBean(IsoLanguagesMapper.getInstance());
				topConcept.setThesaurusInfo(thesaurusMan.getThesaurusByName(sThesaurusName));
				topConcept.setValue("Top Concept(s)", langForThesaurus);
				topConcept.setUriCode(sThesaurusName);
        Element root = KeywordsSearcher.toRawElement(new Element("descKeys"), topConcept);

				Element keywordType = new Element("narrower");
				for (KeywordBean kbr : kbList) {
					keywordType.addContent(kbr.toElement(context.getLanguage()));
				}
				root.addContent(keywordType);
        
        return root;
    }
}

// =============================================================================

