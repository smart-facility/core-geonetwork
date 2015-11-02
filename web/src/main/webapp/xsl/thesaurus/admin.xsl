<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
							  xmlns:fo="http://www.w3.org/1999/XSL/Format">


	<xsl:include href="../header.xsl"/>
	<xsl:include href="../banner.xsl"/>

	<xsl:variable name="indent" select="100"/>
  <xsl:variable name="baseUrl" select="/root/gui/url" />
	<xsl:variable name="widgetPath">../../apps</xsl:variable>


	<xsl:template mode="css" match="/" priority="2">
		<link rel="stylesheet" href="{concat($baseUrl, '/static/geonetwork-client_css.css')}"></link>
	</xsl:template>

	<xsl:template mode="script" match="/" priority="2">
								<xsl:choose>
                     <xsl:when test="/root/gui/config/map/osm_map = 'true'">
                         <script>
                             var useOSMLayers = true;
                         </script>
                     </xsl:when>

                     <xsl:otherwise>
                         <script>
                             var useOSMLayers = false;
                         </script>
                     </xsl:otherwise>
                 </xsl:choose>

		<xsl:variable name="minimize">
           <xsl:choose>
             <xsl:when test="/root/request/debug">?minimize=false</xsl:when>
             <xsl:otherwise></xsl:otherwise>
          </xsl:choose>
    </xsl:variable>

		<script type="text/javascript" src="{concat($baseUrl, '/static/geonetwork-client-mini-nomap.js', $minimize)}"></script>

		<script type="text/javascript" language="JavaScript">
			var catalogue;

			OpenLayers.ProxyHostURL = '../../proxy?url=';

			OpenLayers.ProxyHost = function(url){
				/**
				 * Do not use proxy for local domain.
				 * This is required to keep the session activated.
				 */
				if (url &amp;&amp; url.indexOf(window.location.host) != -1) {
					return url;
				} else {
					
					return OpenLayers.ProxyHostURL + encodeURIComponent(url);
					
				}
			};
			Ext.onReady(function(){
				GeoNetwork.Util.setLang('<xsl:value-of select="/root/gui/language"/>', '<xsl:value-of select="$widgetPath"/>');

				catalogue = new GeoNetwork.Catalogue({
					statusBarId : 'info',
					hostUrl: '../..',
					lang: '<xsl:value-of select="/root/gui/language"/>',
					mdOverlayedCmpId : 'resultsPanel'
				});

				var manager = new GeoNetwork.admin.ThesaurusManagerPanel({
					catalogue: catalogue,
					feed: '<xsl:value-of select="/root/gui/config/repository/thesaurus"/>',
					renderTo: 'manager',
					autoWidth : true,
					layout : 'border',
					height: 680
				});
			})
		</script>
	</xsl:template>

	<xsl:template match="/">
		<html>
			<head>
				<xsl:call-template name="header"/>
				<xsl:apply-templates mode="script" select="/"/>

				<style type="text/css">
					body {
						height:100%;
					}
				</style>
			</head>
			<body>
				<xsl:call-template name="banner"/>
				<div id="content_container">
					<xsl:call-template name="content"/>
				</div>
			</body>
		</html>
	</xsl:template>

	<xsl:template name="content">

		<table	width="100%" height="100%">
			<tr>
				<td class="padded-content" width="{$indent}"/>
				<td class="dots"/>
				<td class="padded-content" style="height:25px;">
					<h1><xsl:value-of select="/root/gui/strings/thesaurus/management"/></h1>
				</td>
			</tr>
			<tr>
				<td class="padded-content" width="{$indent}"/>
				<td class="dots"/>
				<td style="padding:5px;">
					<div id="manager" style="width:100%;" align="left"/>
				</td>
			</tr>
			<tr>
				<td class="padded-content" width="{$indent}" style="height:25px;"/>
				<td class="dots"/>
				<td class="padded-content" style="text-align:center;">
					<button class="content" onclick="load('{/root/gui/locService}/admin')">
						<xsl:value-of select="/root/gui/strings/back"/>
					</button>
				</td>
			</tr>
			<tr><td class="blue-content" colspan="3"/></tr>
		</table> 
	</xsl:template>

</xsl:stylesheet>
