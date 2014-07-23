package com.hof.mi.web.service;

import java.io.Serializable;
import java.math.BigDecimal;

public class ReportServiceResponse
implements Serializable
{
public static final String cvsId = "$Id: ReportServiceResponse.java,v 1.30 2012-05-08 01:01:07 al Exp $";
private Integer ReportId = null;
private String ReportUUID = null;
private String StatusCode = null;
private Integer ErrorCode = null;
private String FormatCode = null;
private BigDecimal LastRunTime = null;
private String ReportType = null;
private String LastRunStatus = null;
private Integer LastRunDuration = null;
private String ReportName = null;
private String ReportDescription = null;
private Integer HitCount = null;
private String BinaryData = null;
private ReportRow[] Results = null;
private ReportSchema[] Columns = null;
private String ContentType = null;
private String[] Messages = null;
private ReportChart[] Charts = null;
private ReportComment[] Comments = null;
private ReportBinaryObject[] BinaryObjects = null;
private String SessionId = null;
private String AuthoringMode = null;
private String ReportTemplate = null;
private String DataOutput = null;
private String PreRunFilterString = null;
private boolean DashboardEnabled = false;
private String ReportStyle = null;
private String subCategory = null;
private String category = null;
private String author = null;
private Integer averageRunTime = null;
private String tags = null;
private String datasource = null;
private String viewName = null;
private String lastModifiedDate = null;
private DashboardDefinition dashboard = null;
private Integer[] modifiedReports = null;
private Integer reportUsage = null;
private BreadCrumb[] breadcrumbs = null;
private SeriesSelection[] seriesSelection = null;
private TimeAggregationSelection[] timeAggregationSelection = null;
private ReportTabSelection[] reportTabSelection = null;
private ReportPageSelection[] reportPageSelection = null;
private ScheduleRecord schedule = null;
private TimeSliderSelection[] timeSliderSelection = null;
private SortableTableColumn[] sortableColumns = null;
private RelatedReports relatedReports = null;
private Integer selectedSortColumn = null;
private Integer selectedSortOrder = null;
private String drillCode = null;
private Boolean canDrill = Boolean.FALSE;
private KPI KPI = null;
private String[] drillAnywhereCategories = null;
private DrillAnywhereTargets[] drillAnywhereTargets = null;
private ReportFilter[] reportFilters = null;
private GISMap[] gisMap = null;
private GMap[] googleMaps = null;
private Integer displayedReportId = null;
private Object[] storyboardDescriptors = null;

public Integer[] getModifiedReports()
{
return this.modifiedReports;
}

public Integer getReportUsage()
{
return this.reportUsage;
}

public void setReportUsage(Integer reportUsage)
{
this.reportUsage = reportUsage;
}

public void setModifiedReports(Integer[] modifiedReports)
{
this.modifiedReports = modifiedReports;
}

public DashboardDefinition getDashboard()
{
return this.dashboard;
}

public void setDashboard(DashboardDefinition dashboard)
{
this.dashboard = dashboard;
}

public String getBinaryData()
{
return this.BinaryData;
}

public void setBinaryData(String binaryData)
{
this.BinaryData = binaryData;
}

public String getFormatCode()
{
return this.FormatCode;
}

public void setFormatCode(String formatCode)
{
this.FormatCode = formatCode;
}

public Integer getHitCount()
{
return this.HitCount;
}

public void setHitCount(Integer hitCount)
{
this.HitCount = hitCount;
}

public Integer getLastRunDuration()
{
return this.LastRunDuration;
}

public void setLastRunDuration(Integer lastRunDuration)
{
this.LastRunDuration = lastRunDuration;
}

public String getLastRunStatus()
{
return this.LastRunStatus;
}

public void setLastRunStatus(String lastRunStatus)
{
this.LastRunStatus = lastRunStatus;
}

public BigDecimal getLastRunTime()
{
return this.LastRunTime;
}

public void setLastRunTime(BigDecimal lastRunTime)
{
this.LastRunTime = lastRunTime;
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
return this.ReportName;
}

public void setReportName(String reportName)
{
this.ReportName = reportName;
}

public String getReportType()
{
return this.ReportType;
}

public void setReportType(String reportType)
{
this.ReportType = reportType;
}

public String getReportUUID()
{
return this.ReportUUID;
}

public void setReportUUID(String reportUUID)
{
this.ReportUUID = reportUUID;
}

public String getStatusCode()
{
return this.StatusCode;
}

public void setStatusCode(String statusCode)
{
this.StatusCode = statusCode;
}

public ReportSchema[] getColumns()
{
return this.Columns;
}

public void setColumns(ReportSchema[] columns)
{
this.Columns = columns;
}

public ReportRow[] getResults()
{
return this.Results;
}

public void setResults(ReportRow[] results)
{
this.Results = results;
}

public String getContentType()
{
return this.ContentType;
}

public void setContentType(String contentType)
{
this.ContentType = contentType;
}

public String[] getMessages()
{
return this.Messages;
}

public void setMessages(String[] messages)
{
this.Messages = messages;
}

public ReportChart[] getCharts()
{
return this.Charts;
}

public void setCharts(ReportChart[] charts)
{
this.Charts = charts;
}

public String getSessionId()
{
return this.SessionId;
}

public void setSessionId(String sessionId)
{
this.SessionId = sessionId;
}

public ReportBinaryObject[] getBinaryObjects()
{
return this.BinaryObjects;
}

public void setBinaryObjects(ReportBinaryObject[] binaryObjects)
{
this.BinaryObjects = binaryObjects;
}

public Integer getErrorCode()
{
return this.ErrorCode;
}

public void setErrorCode(Integer errorCode)
{
this.ErrorCode = errorCode;
}

public String getAuthoringMode()
{
return this.AuthoringMode;
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
return this.DataOutput;
}

public void setDataOutput(String dataOutput)
{
this.DataOutput = dataOutput;
}

public String getReportTemplate()
{
return this.ReportTemplate;
}

public void setReportTemplate(String reportTemplate)
{
this.ReportTemplate = reportTemplate;
}

public String getPreRunFilterString()
{
return this.PreRunFilterString;
}

public void setPreRunFilterString(String preRunFilterString)
{
this.PreRunFilterString = preRunFilterString;
}

public String getReportStyle()
{
return this.ReportStyle;
}

public void setReportStyle(String reportStyle)
{
this.ReportStyle = reportStyle;
}

public String getReportDescription()
{
return this.ReportDescription;
}

public void setReportDescription(String reportDescription)
{
this.ReportDescription = reportDescription;
}

public String getSubCategory()
{
return this.subCategory;
}

public void setSubCategory(String subCategory)
{
this.subCategory = subCategory;
}

public String getCategory()
{
return this.category;
}

public void setCategory(String category)
{
this.category = category;
}

public String getAuthor()
{
return this.author;
}

public void setAuthor(String author)
{
this.author = author;
}

public Integer getAverageRunTime()
{
return this.averageRunTime;
}

public void setAverageRunTime(Integer averageRunTime)
{
this.averageRunTime = averageRunTime;
}

public ReportComment[] getComments()
{
return this.Comments;
}

public void setComments(ReportComment[] comments)
{
this.Comments = comments;
}

public void setTags(String tags)
{
this.tags = tags;
}

public String getTags()
{
return this.tags;
}

public void setDatasource(String datasource)
{
this.datasource = datasource;
}

public String getDatasource()
{
return this.datasource;
}

public void setViewName(String viewName)
{
this.viewName = viewName;
}

public String getViewName()
{
return this.viewName;
}

public void setLastModifiedDate(String lastModifiedDate)
{
this.lastModifiedDate = lastModifiedDate;
}

public String getLastModifiedDate()
{
return this.lastModifiedDate;
}

public void setBreadcrumbs(BreadCrumb[] breadcrumbs)
{
this.breadcrumbs = breadcrumbs;
}

public BreadCrumb[] getBreadcrumbs()
{
return this.breadcrumbs;
}

public void setSeriesSelection(SeriesSelection[] seriesSelection)
{
this.seriesSelection = seriesSelection;
}

public SeriesSelection[] getSeriesSelection()
{
return this.seriesSelection;
}

public TimeAggregationSelection[] getTimeAggregationSelection()
{
return this.timeAggregationSelection;
}

public void setTimeAggregationSelection(TimeAggregationSelection[] timeAggregationSelection)
{
this.timeAggregationSelection = timeAggregationSelection;
}

public ReportPageSelection[] getReportPageSelection()
{
return this.reportPageSelection;
}

public void setReportPageSelection(ReportPageSelection[] reportPageSelection)
{
this.reportPageSelection = reportPageSelection;
}

public ReportTabSelection[] getReportTabSelection()
{
return this.reportTabSelection;
}

public void setReportTabSelection(ReportTabSelection[] reportTabSelection)
{
this.reportTabSelection = reportTabSelection;
}

public void setSchedule(ScheduleRecord schedule)
{
this.schedule = schedule;
}

public ScheduleRecord getSchedule()
{
return this.schedule;
}

public TimeSliderSelection[] getTimeSliderSelection()
{
return this.timeSliderSelection;
}

public void setTimeSliderSelection(TimeSliderSelection[] timeSliderSelection)
{
this.timeSliderSelection = timeSliderSelection;
}

public Integer getSelectedSortColumn()
{
return this.selectedSortColumn;
}

public void setSelectedSortColumn(Integer selectedSortColumn)
{
this.selectedSortColumn = selectedSortColumn;
}

public Integer getSelectedSortOrder()
{
return this.selectedSortOrder;
}

public void setSelectedSortOrder(Integer selectedSortOrder)
{
this.selectedSortOrder = selectedSortOrder;
}

public RelatedReports getRelatedReports()
{
return this.relatedReports;
}

public void setRelatedReports(RelatedReports relatedReports)
{
this.relatedReports = relatedReports;
}

public SortableTableColumn[] getSortableColumns()
{
return this.sortableColumns;
}

public void setSortableColumns(SortableTableColumn[] sortableColumns)
{
this.sortableColumns = sortableColumns;
}

public String getDrillCode()
{
return this.drillCode;
}

public void setDrillCode(String drillCode)
{
this.drillCode = drillCode;
}

public Integer getDisplayedReportId()
{
return this.displayedReportId;
}

public void setDisplayedReportId(Integer displayedReportId)
{
this.displayedReportId = displayedReportId;
}

public KPI getKPI()
{
return this.KPI;
}

public void setKPI(KPI kPI)
{
this.KPI = kPI;
}

public Boolean getCanDrill()
{
return this.canDrill;
}

public void setCanDrill(Boolean canDrill)
{
this.canDrill = canDrill;
}

public String[] getDrillAnywhereCategories()
{
return this.drillAnywhereCategories;
}

public void setDrillAnywhereCategories(String[] drillAnywhereCategories)
{
this.drillAnywhereCategories = drillAnywhereCategories;
}

public DrillAnywhereTargets[] getDrillAnywhereTargets()
{
return this.drillAnywhereTargets;
}

public void setDrillAnywhereTargets(DrillAnywhereTargets[] drillAnywhereTargets)
{
this.drillAnywhereTargets = drillAnywhereTargets;
}

public Object[] getStoryboardDescriptors()
{
return this.storyboardDescriptors;
}

public void setStoryboardDescriptors(Object[] storyboardDescriptors)
{
this.storyboardDescriptors = storyboardDescriptors;
}

public GISMap[] getGisMap()
{
return this.gisMap;
}

public void setGisMap(GISMap[] gisMap)
{
this.gisMap = gisMap;
}

public GMap[] getGoogleMaps()
{
return this.googleMaps;
}

public void setGoogleMaps(GMap[] googleMaps)
{
this.googleMaps = googleMaps;
}

public ReportFilter[] getReportFilters()
{
return this.reportFilters;
}

public void setReportFilters(ReportFilter[] reportFilters)
{
this.reportFilters = reportFilters;
}
}
