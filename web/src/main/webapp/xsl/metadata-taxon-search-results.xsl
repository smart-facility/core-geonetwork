<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:app="http://biodiversity.org.au/xml/servicelayer/content"
	xmlns:ibis="http://biodiversity.org.au/xml/ibis">

	<!-- Simplify output from ibis taxon services so that we can handle this
	     using javascript -->

	<xsl:output method="xml" indent="yes"/>

	<xsl:template match="/">
		<root>
			<xsl:apply-templates select="//ibis:search-results/ibis:search-result"/>
		</root>
	</xsl:template>

	<!-- =================================================================== -->
	
	<xsl:template match="ibis:search-result">
		<result>
			<score>
				<xsl:value-of select="score"/>
			</score>
			<lsid>
				<xsl:value-of select="ibis:result-name-ref/@ibis:lsidRef"/>
			</lsid>
			<uri>
				<xsl:value-of select="ibis:result-name-ref/@ibis:uriRef"/>
			</uri>
			<name>
				<xsl:value-of select="ibis:result-name-ref/ibis:NameComplete"/>
			</name>
		</result>
	</xsl:template>

	<!-- =================================================================== -->

</xsl:stylesheet>
