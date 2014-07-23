package com.hof.mi.web.service;

 import com.hof.util.Base64;
 import java.io.Serializable;
 import java.util.Date;

 public class AdministrationReport
 implements Serializable
 {
 public static final String cvsId = "$Id: AdministrationReport.java,v 1.13 2012-04-19 03:21:44 al Exp $";
 private String ReportName = null;
 private String ReportUUID = null;
 private String ReportDescription = null;
 private Integer ReportId = null;
 private String ExecutionObject = null;
 private String ReportCategory = null;
 private String ReportSubCategory = null;
 private String BirtData = null;
 private String SourceName = null;
 private Integer SourceId = null;
 private String AuthoringMode = null;
 private String ReportTemplate = null;
 private String DataOutput = null;
 private boolean DashboardEnabled = false;
 private Integer ViewId = null;
 private String ViewName = null;
 private String ViewDescription = null;
 private String LastModifierName = null;
 private Integer LastModifierId = null;
 private Date LastModifiedDate = null;
 private Date PublishDate = null;
 private String DeliveryMode = null;
 private Integer LastRunTime = null;
 private Integer AverageRunTime = null;
 private String RoleCode = null;
 private String ChartTypeCode = null;
 private Integer Usage = null;

 public Integer getUsage()
 {
 return this.Usage;
 }

 public void setUsage(Integer usage)
 {
 this.Usage = usage;
 }

 private String convertNull(String in)
 {
 return in == null ? "" : in;
 }

 public String getExecutionObject()
 {
 return convertNull(this.ExecutionObject);
 }

 public void setExecutionObject(String executionObject)
 {
 this.ExecutionObject = executionObject;
 }

 public String getReportCategory()
 {
 return convertNull(this.ReportCategory);
 }

 public void setReportCategory(String reportCategory)
 {
 this.ReportCategory = reportCategory;
 }

 public String getReportDescription()
 {
 return convertNull(this.ReportDescription);
 }

 public void setReportDescription(String reportDescription)
 {
 this.ReportDescription = reportDescription;
 }

 public String getReportUUID()
 {
 return convertNull(this.ReportUUID);
 }

 public void setReportUUID(String reportUUID)
 {
 this.ReportUUID = reportUUID;
 }

 public Integer getReportId()
 {
 return this.ReportId;
 }

 public void setReportId(Integer reportId)
 {
 this.ReportId = reportId;
 }

 public String getReportName()
 {
 return convertNull(this.ReportName);
 }

 public void setReportName(String reportName)
 {
 this.ReportName = reportName;
 }

 public String getReportSubCategory()
 {
 return convertNull(this.ReportSubCategory);
 }

 public void setReportSubCategory(String reportSubCategory)
 {
 this.ReportSubCategory = reportSubCategory;
 }

 public String getBirtData()
 {
 return convertNull(this.BirtData);
 }

 public void setBirtData(String birtData)
 {
 this.BirtData = Base64.encodeBytes(birtData.getBytes());
 }

 public void setBirtData(byte[] birtData)
 {
 this.BirtData = Base64.encodeBytes(birtData);
 }

 public Integer getSourceId()
 {
 return this.SourceId;
 }

 public void setSourceId(Integer sourceId)
 {
 this.SourceId = sourceId;
 }

 public String getSourceName()
 {
 return convertNull(this.SourceName);
 }

 public void setSourceName(String sourceName)
 {
 this.SourceName = sourceName;
 }

 public String getAuthoringMode()
 {
 return convertNull(this.AuthoringMode);
 }

 public void setAuthoringMode(String authoringMode)
 {
 this.AuthoringMode = authoringMode;
 }

 public boolean isDashboardEnabled()
 {
 return this.DashboardEnabled;
 }

 public void setDashboardEnabled(boolean dashboardEnabled)
 {
 this.DashboardEnabled = dashboardEnabled;
 }

 public String getDataOutput()
 {
 return convertNull(this.DataOutput);
 }

 public void setDataOutput(String dataOutput)
 {
 this.DataOutput = dataOutput;
 }

 public String getReportTemplate()
 {
 return convertNull(this.ReportTemplate);
 }

 public void setReportTemplate(String reportTemplate)
 {
 this.ReportTemplate = reportTemplate;
 }

 public String getViewName()
 {
 return convertNull(this.ViewName);
 }

 public void setViewName(String viewName)
 {
 this.ViewName = viewName;
 }

 public String getLastModifierName()
 {
 return convertNull(this.LastModifierName);
 }

 public void setLastModifierName(String lastModifierName)
 {
 this.LastModifierName = lastModifierName;
 }

 public Integer getLastModifierId()
 {
 return this.LastModifierId;
 }

 public void setLastModifierId(Integer lastModifierId)
 {
 this.LastModifierId = lastModifierId;
 }

 public Date getLastModifiedDate()
 {
 return this.LastModifiedDate;
 }

 public void setLastModifiedDate(Date lastModifiedDate)
 {
 this.LastModifiedDate = lastModifiedDate;
 }

 public Date getPublishDate()
 {
 return this.PublishDate;
 }

 public void setPublishDate(Date publishDate)
 {
 this.PublishDate = publishDate;
 }

 public Integer getViewId()
 {
 return this.ViewId;
 }

 public void setViewId(Integer viewId)
 {
 this.ViewId = viewId;
 }

 public String getViewDescription()
 {
 return convertNull(this.ViewDescription);
 }

 public void setViewDescription(String viewDescription)
 {
 this.ViewDescription = viewDescription;
 }

 public String getDeliveryMode()
 {
 return convertNull(this.DeliveryMode);
 }

 public void setDeliveryMode(String deliveryMode)
 {
 this.DeliveryMode = deliveryMode;
 }

 public Integer getLastRunTime()
 {
 return this.LastRunTime;
 }

 public void setLastRunTime(Integer lastRunTime)
 {
 this.LastRunTime = lastRunTime;
 }

 public Integer getAverageRunTime()
 {
 return this.AverageRunTime;
 }

 public void setAverageRunTime(Integer averageRunTime)
 {
 this.AverageRunTime = averageRunTime;
 }

 public String getRoleCode()
 {
 return convertNull(this.RoleCode);
 }

 public void setRoleCode(String roleCode)
 {
 this.RoleCode = roleCode;
 }

 public String getChartTypeCode()
 {
 return null != this.ChartTypeCode ? this.ChartTypeCode : "";
 }

 public void setChartTypeCode(String typeCode)
 {
 this.ChartTypeCode = typeCode;
 }
 }
