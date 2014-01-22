<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="text"/>

<xsl:template match="/">
	<xsl:for-each select="select/option">
		<xsl:value-of select="."/><xsl:text>#</xsl:text><xsl:value-of select="substring-before(substring-after(@value,'http://creativecommons.org/international/'),'/')"/><xsl:text>&#x0a;</xsl:text> 
	</xsl:for-each>
</xsl:template>

</xsl:stylesheet>
