package org.fao.geonet.kernel.harvest.harvester.yellowfin;

import com.hof.mi.web.service.i4Report;

class SmallReport {
	String tags = "";
	String executionObject = "";
	String subCategory = "";
	String category = "";
	String author = "";
	String authoringMode = "";
	String binaryURL = "";
	String contentType = "";
	String dataOutput = "";
	String datasource = "";
	String imageURL = "";
	String lastModifiedDate = "";
	String lastRunTime = "";
	String linkURL = "";
	String viewName = "";
	String isGisNavigation = "";

	public SmallReport(i4Report r) {
		this.tags = r.getTags();
		this.subCategory = r.getSubCategory();
		this.category = r.getCategory();
		this.author = r.getAuthor();
		this.authoringMode = r.getAuthoringMode();
		this.executionObject = r.getExecutionObject();
		this.viewName = r.getViewName();
		this.binaryURL = r.getBinaryURL();
		this.contentType = r.getContentType();
		this.dataOutput = r.getDataOutput();
		this.datasource = r.getDatasource();
		this.imageURL = r.getImageURL();
		this.lastModifiedDate = r.getLastModifiedDate();
		this.isGisNavigation = r.isGisNavigation()+"";
		if (r.getLastRunTime() != null) {
			this.lastRunTime = r.getLastRunTime().toString();
		}
		this.linkURL = r.getLinkURL();
	}
}
