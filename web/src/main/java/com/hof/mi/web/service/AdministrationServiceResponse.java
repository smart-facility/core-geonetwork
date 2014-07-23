package com.hof.mi.web.service;

import java.io.Serializable;
import org.apache.log4j.Category;

public class AdministrationServiceResponse
implements Serializable
{
public static final String cvsId = "$Id: AdministrationServiceResponse.java,v 1.13 2012-06-07 00:03:47 peterd Exp $";
static Category log = Category.getInstance(AdministrationServiceResponse.class.getName());
private Integer ReportId = null;
private String StatusCode = null;
private Integer ErrorCode = null;
private Integer EntityId = null;
private String[] Messages = null;
private String SessionId = null;
private String LoginSessionId = null;
private AdministrationPerson person = null;
private AdministrationPerson[] people = null;
private AdministrationGroup group = null;
private AdministrationGroup[] groups = null;
private AdministrationRole[] roles = null;
private AdministrationReport[] reports = null;
private AdministrationReportGroup[] reportGroups = null;
private AdministrationReport report = null;
private AdministrationClientOrg[] clients = null;
private AdministrationClientOrg client = null;
private PersonFavourite[] personfavourites = null;
private ReportBinaryObject[] binaryAttachments = null;
private ContentResource[] contentResources = null;
private ImportIssue[] importIssues = null;

public Integer getReportId()
{
return this.ReportId;
}

public void setReportId(Integer reportId)
{
this.ReportId = reportId;
}

public String getStatusCode()
{
return this.StatusCode;
}

public void setStatusCode(String statusCode)
{
this.StatusCode = statusCode;
}

public String[] getMessages()
{
return this.Messages;
}

public void setMessages(String[] messages)
{
this.Messages = messages;
}

public String getSessionId()
{
return this.SessionId;
}

public void setSessionId(String sessionId)
{
this.SessionId = sessionId;
}

public AdministrationPerson getPerson()
{
return this.person;
}

public void setPerson(AdministrationPerson person)
{
this.person = person;
}

public String getLoginSessionId()
{
return this.LoginSessionId;
}

public void setLoginSessionId(String loginSessionId)
{
this.LoginSessionId = loginSessionId;
}

public AdministrationReport[] getReports()
{
return this.reports;
}

public void setReports(AdministrationReport[] reports)
{
this.reports = reports;
}

public AdministrationGroup getGroup()
{
return this.group;
}

public void setGroup(AdministrationGroup group)
{
this.group = group;
}

public AdministrationGroup[] getGroups()
{
return this.groups;
}

public void setRoles(AdministrationRole[] roles)
{
this.roles = roles;
}

public AdministrationRole[] getRoles()
{
return this.roles;
}

public void setGroups(AdministrationGroup[] groups)
{
this.groups = groups;
}

public AdministrationReport getReport()
{
return this.report;
}

public void setReport(AdministrationReport report)
{
this.report = report;
}

public Integer getErrorCode()
{
return this.ErrorCode;
}

public Integer getEntityId()
{
return this.EntityId;
}

public void setEntityId(Integer entityId)
{
this.EntityId = entityId;
}

public AdministrationClientOrg getClient()
{
return this.client;
}

public void setClient(AdministrationClientOrg client)
{
this.client = client;
}

public AdministrationClientOrg[] getClients()
{
return this.clients;
}

public void setClients(AdministrationClientOrg[] clients)
{
this.clients = clients;
}

public AdministrationPerson[] getPeople()
{
return this.people;
}

public void setPeople(AdministrationPerson[] people)
{
this.people = people;
}

public AdministrationReportGroup[] getReportGroups()
{
return this.reportGroups;
}

public void setReportGroups(AdministrationReportGroup[] reportGroups)
{
this.reportGroups = reportGroups;
}

public void setPersonfavourites(PersonFavourite[] personfavourites)
{
this.personfavourites = personfavourites;
}

public PersonFavourite[] getPersonfavourites()
{
return this.personfavourites;
}

public void setBinaryAttachments(ReportBinaryObject[] binaryAttachments)
{
this.binaryAttachments = binaryAttachments;
}

public ReportBinaryObject[] getBinaryAttachments()
{
return this.binaryAttachments;
}

public ContentResource[] getContentResources()
{
return this.contentResources;
}

public void setContentResources(ContentResource[] contentResources)
{
this.contentResources = contentResources;
}

public ImportIssue[] getImportIssues()
{
return this.importIssues;
}

public void setImportIssues(ImportIssue[] importIssues)
{
this.importIssues = importIssues;
}
}
