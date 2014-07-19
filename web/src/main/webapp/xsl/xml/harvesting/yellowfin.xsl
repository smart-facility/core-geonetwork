<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- ==================================================================== -->

	<xsl:import href="common.xsl"/>	

	<!-- ==================================================================== -->
	<!-- === Metadata fragments harvesting node -->
	<!-- ==================================================================== -->

	<xsl:template match="*" mode="site">
		<hostname><xsl:value-of select="hostname/value" /></hostname>
		<port><xsl:value-of select="port/value" /></port>
		<icon><xsl:value-of select="icon/value" /></icon>
	</xsl:template>

	<!-- ==================================================================== -->

	<xsl:template match="*" mode="options">
		<outputSchema><xsl:value-of  select="outputSchema/value" /></outputSchema>
		<stylesheet><xsl:value-of  select="stylesheet/value" /></stylesheet>
		<templateId><xsl:value-of  select="templateId/value" /></templateId>
	</xsl:template>

	<!-- ==================================================================== -->

	<xsl:template match="*" mode="searches">
		<searches>
		  <xsl:for-each select="children/search">
				<search>
					<freeText><xsl:value-of select="children/freeText/value" /></freeText>
				</search>
			</xsl:for-each>
		</searches>
	</xsl:template>

	<!-- ==================================================================== -->

</xsl:stylesheet>
