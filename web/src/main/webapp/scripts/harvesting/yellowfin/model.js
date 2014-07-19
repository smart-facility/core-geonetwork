//=====================================================================================
//===
//=== Model (type:yellowfin)
//===
//=====================================================================================

yellowfin.Model = function(xmlLoader)
{
	HarvesterModel.call(this);	

	var loader    = xmlLoader;
	var callBackSchemas = null;
	var callBackStyleSheets = null;

	this.retrieveGroups    = retrieveGroups;
	this.retrieveCategories= retrieveCategories;
	this.retrieveIcons     = retrieveIcons;
	this.getUpdateRequest  = getUpdateRequest;
	this.retrieveSchemas   = retrieveSchemas;

//=====================================================================================

function retrieveSchemas(callBack)
{
	callBackSchemas = callBack;	

	var request = ker.createRequest('type', 'yellowfinSchemas');
	
	ker.send('xml.harvesting.info', request, ker.wrap(this, retrieveSchemas_OK));
}

//-------------------------------------------------------------------------------------

function retrieveSchemas_OK(xmlRes)
{
	if (xmlRes.nodeName == 'error')
		ker.showError(loader.getText('cannotRetrieve'), xmlRes);
	else
	{
		var data = [];
		var list = xml.children(xml.children(xmlRes)[0]);
		
		for (var i=0; i<list.length; i++) {
			data.push(xml.toObject(list[i]));
		}
		
		callBackSchemas(data);
	}
}

//=====================================================================================

function retrieveGroups(callBack)
{
	new InfoService(loader, 'groupsIncludingSystemGroups', callBack);
}

//=====================================================================================

function retrieveCategories(callBack)
{
	new InfoService(loader, 'categories', callBack);
}

//=====================================================================================

function retrieveIcons(callBack)
{
	callBackF = callBack;	

	var request = ker.createRequest('type', 'icons');
	
	ker.send('xml.harvesting.info', request, ker.wrap(this, retrieveIcons_OK));
}

//-------------------------------------------------------------------------------------

function retrieveIcons_OK(xmlRes)
{
	if (xmlRes.nodeName == 'error')
		ker.showError(loader.getText('cannotRetrieve'), xmlRes);
	else
	{
		var data = [];
		var list = xml.children(xml.children(xmlRes)[0]);
		
		for (var i=0; i<list.length; i++)
			data.push(xml.textContent(list[i]));
		
		callBackF(data);
	}
}

//=====================================================================================

function getUpdateRequest(data)
{
	var request = str.substitute(updateTemp, data);
	
	var list = data.SEARCH_LIST;
	var text = '';
		
	for (var i=0; i<list.length; i++)
		text += str.substitute(searchTemp, list[i]);
	
	request = str.replace(request, '{SEARCH_LIST}', text);
	
	return this.substituteCommon(data, request);
}

//=====================================================================================

var updateTemp = 
' <node id="{ID}" type="{TYPE}">'+ 
'    <ownerGroup><id>{OWNERGROUP}</id></ownerGroup>'+
'    <site>'+
'      <name>{NAME}</name>'+
'      <hostname>{HOSTNAME}</hostname>'+
'      <port>{PORT}</port>'+
'      <icon>{ICON}</icon>'+
'      <account>'+
'        <use>{USE_ACCOUNT}</use>'+
'        <username>{USERNAME}</username>'+
'        <password>{PASSWORD}</password>'+
'      </account>'+
'    </site>'+
    
'    <options>'+
'      <oneRunOnly>{ONE_RUN_ONLY}</oneRunOnly>'+
'      <every>{EVERY}</every>'+
'      <outputSchema>{OUTPUTSCHEMA}</outputSchema>'+
'      <stylesheet>{STYLESHEET}</stylesheet>'+
'      <templateId>{TEMPLATEID}</templateId>'+
'    </options>'+

'    <content>'+
'      <validate>{VALIDATE}</validate>'+
'      <importxslt>{IMPORTXSLT}</importxslt>'+
'    </content>'+

'    <searches>'+
'       {SEARCH_LIST}'+
'    </searches>'+

'    <privileges>'+
'       {PRIVIL_LIST}'+
'    </privileges>'+

'    <categories>'+
'       {CATEG_LIST}'+
'    </categories>'+
'  </node>';

//=====================================================================================

var searchTemp = 
'    <search>'+
'      <freeText>{ANY_TEXT}</freeText>'+
'    </search>';


//=====================================================================================
}
