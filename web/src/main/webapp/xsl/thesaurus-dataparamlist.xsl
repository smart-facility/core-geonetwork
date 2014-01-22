<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method='html' encoding='UTF-8' indent='yes'/>

<xsl:template match="/">

<xsl:variable name="lcletters">abcdefghijklmnopqrstuvwxyz</xsl:variable>
<xsl:variable name="ucletters">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>

<xsl:variable name="in" select="translate(/root/request/dataparamCompleter,$ucletters,$lcletters)"/>
<xsl:variable name="mode" select="/root/request/mode"/>

	<xsl:choose>
		<xsl:when test="$mode = 'selector'">
				<img align="right" src="{/root/gui/url}/images/del.gif" onclick="$('dataparamSelectorFrame').style.display = 'none'"/><br/>
				<xsl:if test="count(/root/response/summary/dataparams/dataparam)=0">0 <xsl:value-of select="/root/gui/schemas/iso19139.mcp/strings/dataparam"/>.</xsl:if>

				<xsl:for-each select="/root/response/summary/dataparams/dataparam">
					<input type="checkbox" name="" value="" onclick="selectorCheck(this.value, this.checked, 'dataparam', 'or');">
						<xsl:attribute name="value">
							<xsl:value-of select="@name"/>
						</xsl:attribute>

						<xsl:if test="contains($in, @name)=1">
							<xsl:attribute name="checked">checked</xsl:attribute>
						</xsl:if>
					</input>
					<xsl:value-of select="@name"/> <span>(<xsl:value-of select="@count"/>
					<xsl:text> </xsl:text>
					<xsl:choose>
						<xsl:when test="@count=2">
							<xsl:value-of select="/root/gui/strings/res"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="/root/gui/strings/ress"/>
						</xsl:otherwise>
					</xsl:choose>)
					</span>
					<br/>
				</xsl:for-each>
		</xsl:when>
		<xsl:otherwise>
			<ul>
				<xsl:for-each select="/root/response/summary/dataparams/dataparam[contains(@name,$in)=1]">
					<li>
						<xsl:value-of select="@name"/>
					</li>
				</xsl:for-each>
			</ul>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

	
</xsl:stylesheet>
