//=====================================================================================
//===
//=== View (type:yellowfin)
//===
//=====================================================================================

yellowfin.View = function(xmlLoader)
{
	HarvesterView.call(this);	
	
	var searchTransf = new XSLTransformer('harvesting/yellowfin/client-search-row.xsl', xmlLoader);
	var privilTransf = new XSLTransformer('harvesting/yellowfin/client-privil-row.xsl', xmlLoader);
	var resultTransf = new XSLTransformer('harvesting/yellowfin/client-result-tip.xsl', xmlLoader);
	
	var loader = xmlLoader;
	var valid  = new Validator(loader);
	var shower = null;
	var selectedSheet = '';
	var selectedTemplateId='';
	
	var currSearchId = 0;
	
	this.setPrefix('yellowfin');
	this.setPrivilTransf(privilTransf);
	this.setResultTransf(resultTransf);
	
	//--- public methods
	
	this.init           = init;
	this.setEmpty       = setEmpty;
	this.setData        = setData;
	this.getData        = getData;
	this.isDataValid    = isDataValid;
	this.clearIcons     = clearIcons;
	this.addIcon        = addIcon;		
	this.addEmptySearch = addEmptySearch;
	this.removeSearch   = removeSearch;

	Event.observe('yellowfin.outputSchema', 'change', ker.wrap(this, changeSchemaOptions));

	Event.observe('yellowfin.icon', 'change', ker.wrap(this, updateIcon));

//=====================================================================================
//===
//=== API methods
//===
//=====================================================================================

function init()
{
	valid.add(
	[
		{ id:'yellowfin.name',        type:'length',   minSize :1,  maxSize :200 },
		{ id:'yellowfin.hostname',    type:'length',   minSize :1,  maxSize :200 },
		{ id:'yellowfin.port',        type:'integer',  minSize: 1,  maxSize :10},
		{ id:'yellowfin.username',    type:'length',   minSize :0,  maxSize :200 },
		{ id:'yellowfin.password',    type:'length',   minSize :0,  maxSize :200 },
		{ id:'yellowfin.outputSchema', type:'length', minSize:1, maxSize: 200 },
		{ id:'yellowfin.templateId', type:'length', minValue:1, maxValue: 200 }
	]);

	shower = new Shower('yellowfin.useAccount', 'yellowfin.account');
}

//=====================================================================================

function setEmpty()
{
	this.setEmptyCommon();
	
	removeAllSearch();
	
	$('yellowfin.hostname').value = '';
	$('yellowfin.outputSchema').value = '';
	$('yellowfin.stylesheet').value = '';
	$('yellowfin.templateId').value = '0';
	
	var icons = $('yellowfin.icon').options;
	
	for (var i=0; i<icons.length; i++)
		if (icons[i].value == 'default.gif')
		{
			icons[i].selected = true;
			break;
		}

	shower.update();
	updateIcon();
}

//=====================================================================================

function setData(node)
{
	this.setDataCommon(node);

	var site     = node.getElementsByTagName('site')    [0];
	var searches = node.getElementsByTagName('searches')[0];
	var options  = node.getElementsByTagName('options') [0];

	hvutil.setOption(site, 'hostname', 'yellowfin.hostname');
	hvutil.setOption(site, 'port',     'yellowfin.port');
	hvutil.setOption(site, 'icon',     'yellowfin.icon');
	hvutil.setOption(options, 'outputSchema',     'yellowfin.outputSchema');
	if ($('yellowfin.outputSchema').selectedIndex > 0) {
	    selectedSheet = hvutil.find(options, 'stylesheet');
			selectedTemplateId = hvutil.find(options, 'templateId');
			changeSchemaOptions();
	}
	
	//--- add search entries
	
	var list = searches.getElementsByTagName('search');
	
	removeAllSearch();
	
	for (var i=0; i<list.length; i++)
		addSearch(list[i]);

	//--- add privileges entries
	
	this.removeAllGroupRows();
	this.addGroupRows(node);
	
	//--- set categories

	this.unselectCategories();
	this.selectCategories(node);	
	
	shower.update();
	updateIcon();
}

//=====================================================================================

function getData()
{
	var data = this.getDataCommon();
	
	data.HOSTNAME = $F('yellowfin.hostname');
	data.PORT     = $F('yellowfin.port');
	data.OUTPUTSCHEMA       = $F('yellowfin.outputSchema');
	data.STYLESHEET         = $F('yellowfin.stylesheet');
	data.TEMPLATEID         = $F('yellowfin.templateId');
	data.ICON     = $F('yellowfin.icon');
	
	//--- retrieve search information
	
	var searchData = [];
	var searchList = xml.children($('yellowfin.searches'));
	
	for(var i=0; i<searchList.length; i++)
	{
		var divElem = searchList[i];
		
		searchData.push(
		{
			ANY_TEXT : xml.getElementById(divElem, 'yellowfin.anytext') .value
		});
	}
	
	data.SEARCH_LIST = searchData;
	
	//--- retrieve privileges and categories information
	
	data.PRIVILEGES = this.getPrivileges();
	data.CATEGORIES = this.getSelectedCategories();
		
	return data;
}

//=====================================================================================

function isDataValid()
{
	if (!valid.validate())
		return false;
		
	return this.isDataValidCommon();
}

//=====================================================================================

function clearIcons() 
{ 
	$('yellowfin.icon').options.length = 0;
}

//=====================================================================================

function addIcon(file)
{
	gui.addToSelect('yellowfin.icon', file, file);
}

//=====================================================================================

function updateIcon()
{
	var icon = $F('yellowfin.icon');
	var image= $('yellowfin.icon.image');
	
	image.setAttribute('src', Env.url +'/images/harvesting/'+icon);
}

//=====================================================================================

function changeSchemaOptions()
{
	var select = $('yellowfin.outputSchema');
	if (select.selectedIndex > 0) {

		this.selectedSchema = select[select.selectedIndex].value;

		// load the stylesheets for the chosen schema

		request = ker.createRequestFromObject({
			type: 'yellowfinStylesheets',
			schema: this.selectedSchema
		});
		ker.send('xml.harvesting.info', request, ker.wrap(this, retrieveStylesheets_OK));

		// load the templates for the chosen schema

		new InfoService(loader, 'templates', ker.wrap(this, updateTemplates_OK));
	}

	$('yellowfinSchemaOptions').show();
}


//=====================================================================================

function retrieveStylesheets_OK(xmlRes)
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
	}

	updateSelectFragmentStylesheets(data);
}

//=====================================================================================

function updateSelectFragmentStylesheets(data)
{
	$('yellowfin.stylesheet').options.length = 0;
	gui.addToSelect('yellowfin.stylesheet', 0, "");

	for (var i=0; i<data.length; i++) {
		var optionValue = '(' + data[i].schema + ') ' + data[i].name;
		if (data[i].id == selectedSheet) {
			gui.addToSelect('yellowfin.stylesheet', data[i].id, optionValue, true);
		} else {
			gui.addToSelect('yellowfin.stylesheet', data[i].id, optionValue);
		}
	}				
}

//=====================================================================================

function updateTemplates_OK(data)
{
	$('yellowfin.templateId').options.length = 0;
	gui.addToSelect('yellowfin.templateId', 0, "");

	for (var i=0; i<data.length; i++) {
		if (data[i].schema == this.selectedSchema) {
			var optionValue = '(' + data[i].schema + ') ' + data[i].title;
			if (data[i].id == selectedTemplateId) {
				gui.addToSelect('yellowfin.templateId', data[i].id, optionValue, true);
			} else {
				gui.addToSelect('yellowfin.templateId', data[i].id, optionValue);
			}
		}
	}				
}


//=====================================================================================
//=== Search methods
//=====================================================================================

function addEmptySearch()
{
	var doc    = Sarissa.getDomDocument();	
	var search = doc.createElement('search');
	
	addSearch(search);
}

//=====================================================================================

function addSearch(search)
{
	var id = ''+ currSearchId++;
	search.setAttribute('id', id);
	
	var html = searchTransf.transformToText(search);

	//--- add the new search in list
	new Insertion.Bottom('yellowfin.searches', html);
	
	valid.add(
	[
		{ id:'yellowfin.anytext',  type:'length',   minSize :0,  maxSize :200 }
	], id);
}

//=====================================================================================

function removeSearch(id)
{
	valid.removeByParent(id);
	Element.remove(id);
}

//=====================================================================================

function removeAllSearch()
{
	$('yellowfin.searches').innerHTML = '';
	valid.removeByParent();	
}

//=====================================================================================
}

