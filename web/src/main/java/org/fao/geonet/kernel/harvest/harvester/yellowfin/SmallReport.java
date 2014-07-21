package org.fao.geonet.kernel.harvest.harvester.yellowfin;

import com.hof.mi.web.service.i4Report;

class SmallReport {
	String tags = "";
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

	public SmallReport(i4Report r) {
		this.tags = r.getTags();
		this.subCategory = r.getSubCategory();
		this.category = r.getCategory();
		this.author = r.getAuthor();
		this.authoringMode = r.getAuthoringMode();
		this.binaryURL = r.getBinaryURL();
		this.contentType = r.getContentType();
		this.dataOutput = r.getDataOutput();
		this.datasource = r.getDatasource();
		this.imageURL = r.getImageURL();
		this.lastModifiedDate = r.getLastModifiedDate();
		if (r.getLastRunTime() != null) {
			this.lastRunTime = r.getLastRunTime().toString();
		}
		this.linkURL = r.getLinkURL();
	}
}
