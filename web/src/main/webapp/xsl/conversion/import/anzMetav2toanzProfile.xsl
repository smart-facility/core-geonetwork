<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"  xmlns="http://www.isotc211.org/2005/gmd" xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:exslt= "http://exslt.org/common" exclude-result-prefixes="exslt">
	<!-- This stylesheet converts ANZMeta V2 metadata into ISO19139 metadata in XML format -->
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />
	<xsl:namespace-alias stylesheet-prefix="#default" result-prefix="gmd"/>

	<xsl:include href="../anzMeta_v2toProfile/anzMeta_v2toProfile.xsl"/>
	
	<xsl:template match="/root">
		<!-- Export anzMeta v2 converting it to ISO19115/19139 XML -->
		<xsl:variable name="md">
			<xsl:apply-templates select="anzmeta"/>
		</xsl:variable>
		<xsl:apply-templates select="exslt:node-set($md)"/>
	</xsl:template>
	
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()[name(self::*)!='geonet:info']"/>
		</xsl:copy>
	</xsl:template>
	
</xsl:stylesheet>
