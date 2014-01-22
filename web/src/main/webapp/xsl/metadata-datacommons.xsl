<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:sc="scaling">

	<xsl:include href="res.xsl"/>

	<xsl:variable name="dcurl" select="/root/gui/strings/dataCommonsUrl"/>

	<xsl:template mode="title" match="/">

		<xsl:value-of select="/root/gui/strings/dataCommonsLicenceOptions"/>
		<script type="text/javascript" src="{/root/gui/url}/scripts/prototype.js"/>
		<script type="text/javascript" src="{/root/gui/url}/scripts/geonetwork.js"/>

	</xsl:template>

	<!--
	page content
	-->
	<xsl:template name="content">
		<xsl:variable name="juris" select="substring-before(substring-after(/root/datacommons/@jurisdiction,concat($dcurl,'/international/')),'/')"/>
		<xsl:variable name="licenseselect">
			<xsl:choose>
				<xsl:when test="/root/datacommons/dynamic">
					<xsl:value-of select="/root/datacommons/dynamic/select"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="/root/gui/static/jurisdiction/@jurisdiction=$juris"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<table width="100%">
			
			<form name="dclicense" accept-charset="UTF-8" action="{/root/gui/locService}/metadata.datacommons.set" method="post" enctype="multipart/form-data">
				<input type="hidden" name="id" value="{/root/datacommons/@id}"/>
				<input type="hidden" name="version" value="{/root/datacommons/@version}"/>
				<input type="hidden" name="type" value="{/root/datacommons/@type}"/>
				<input type="hidden" name="jurisdiction" value="{/root/datacommons/@jurisdiction}"/>

				<xsl:variable name="inurl" select="/root/datacommons/@licenseurl"/>
				<tr>
					<td align="left" colspan="2"><b><xsl:value-of select="/root/gui/datacommons/heading"/></b></td>
				</tr>
				<xsl:for-each select="/root/gui/static/jurisdiction[@name=$juris]/select/option">
					<xsl:variable name="url" select="normalize-space(@value)"/>
					<xsl:variable name="licName" select="normalize-space(.)"/>
					<xsl:variable name="imUrl" select="concat($dcurl,'/',substring-after($url,concat($dcurl,'/licenses/')),'88x31.png')"/>
					<tr>
					<td align="left">
						<xsl:choose>
							<xsl:when test="$inurl=$url or $inurl='none'">
								<input class="content" type="radio" value="{$url}#{$imUrl}#{$licName}" name="choice" checked="true">
									<a href="javascript:popWindow('{$url}')">
										<IMG align="middle" src="{$imUrl}" longdesc="{$url}" alt="{$licName}"></IMG>
									</a>
								</input>	

								<!-- save the original choice to reinstate on cancel -->
								<xsl:if test="$inurl=$url">
									<input name="originalchoice" type="hidden" value="{$url}#{$imUrl}#{$licName}"/>
								</xsl:if>
							</xsl:when>
							<xsl:otherwise>
								<input class="content" type="radio" value="{$url}#{$imUrl}#{$licName}" name="choice">
									<a href="javascript:popWindow('{$url}')">
										<IMG align="middle" src="{$imUrl}" longdesc="{$url}" alt="{$licName}"></IMG>
									</a>
								</input>	
							</xsl:otherwise>
						</xsl:choose>
								
					</td>
					<td align="left">
						<a href="javascript:popWindow('{$url}')"><xsl:value-of select="$licName"/></a>
					</td>
					</tr>
				</xsl:for-each>
				<tr>
					<td align="center" colspan="2" class="dots"/>
				</tr>
				<tr>
					<td align="center" colspan="2">
						<br/>
						<input class="content" type="submit" value="{/root/gui/datacommons/assign}" name="btn"/>
						<!-- cancel button only shown if a choice had already been made -->
						<xsl:if test="$inurl!='none'">
							<xsl:text> </xsl:text>
							<input class="content" type="submit" value="{/root/gui/datacommons/cancel}" name="btn"/>
						</xsl:if>
					</td>
				</tr>
			</form>			
		</table>
	</xsl:template>

	<!-- ================================================================== -->
	
</xsl:stylesheet>
