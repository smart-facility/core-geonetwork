<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<!-- ============================================================================================= -->
	<!-- === editPanel -->
	<!-- ============================================================================================= -->

	<xsl:template name="editPanel-yellowfin">
		<div id="yellowfin.editPanel">
            <xsl:call-template name="ownerGroup-YELLOWFIN"/>
            <div class="dots"/>
			<xsl:call-template name="site-YELLOWFIN"/>
			<div class="dots"/>
			<xsl:call-template name="search-YELLOWFIN"/>
			<div class="dots"/>
			<xsl:call-template name="options-YELLOWFIN"/>
			<div class="dots"/>
			<xsl:call-template name="content-YELLOWFIN"/>
			<div class="dots"/>
			<xsl:call-template name="privileges-YELLOWFIN"/>
			<div class="dots"/>
			<xsl:call-template name="categories-YELLOWFIN"/>
		</div>
	</xsl:template>

    <!-- ============================================================================================= -->
    <xsl:template name="ownerGroup-YELLOWFIN">
        <table border="0">
            <tr>
                <td class="padded"><xsl:value-of select="/root/gui/harvesting/selectownergroup"/></td>
                <td class="padded"><select id="yellowfin.ownerGroup" class="content"/></td>
            </tr>
            <tr>
                <td colspan="2">&#xA0;</td>
            </tr>
        </table>
    </xsl:template>
	<!-- ============================================================================================= -->

	<xsl:template name="site-YELLOWFIN">
		<h1 align="left"><xsl:value-of select="/root/gui/harvesting/site"/></h1>
	
		<table border="0">
			<tr>
				<td class="padded"><xsl:value-of select="/root/gui/harvesting/name"/></td>
				<td class="padded"><input id="yellowfin.name" class="content" type="text" value="" size="30"/></td>
			</tr>

			<tr>
				<td class="padded"><xsl:value-of select="/root/gui/harvesting/yellowfinHostname"/></td>
				<td class="padded"><input id="yellowfin.hostname" class="content" type="text" value="" size="30"/></td>
			</tr>

			<tr>
				<td class="padded"><xsl:value-of select="/root/gui/harvesting/port"/></td>
				<td class="padded"><input id="yellowfin.port" class="content" type="text" value="" size="30"/></td>
			</tr>

			<tr>
				<td class="padded" valign="bottom"><xsl:value-of select="/root/gui/harvesting/icon"/></td>
				<td class="padded">
					<select id="yellowfin.icon" class="content" name="icon" size="1"/>
					&#xA0;
					<img id="yellowfin.icon.image" src="" alt="" class="logo"/>
				</td>
			</tr>
	
			<tr>
				<td class="padded"><xsl:value-of select="/root/gui/harvesting/useAccount"/></td>
				<td class="padded"><input id="yellowfin.useAccount" type="checkbox" checked="on"/></td>
			</tr>

			<tr>
				<td/>
				<td>
					<table id="yellowfin.account">
						<tr>
							<td class="padded"><xsl:value-of select="/root/gui/harvesting/username"/></td>
							<td class="padded"><input id="yellowfin.username" class="content" type="text" value="" size="20"/></td>
						</tr>
		
						<tr>
							<td class="padded"><xsl:value-of select="/root/gui/harvesting/password"/></td>
							<td class="padded"><input id="yellowfin.password" class="content" type="password" value="" size="20"/></td>
						</tr>
					</table>
				</td>
			</tr>			
		</table>
	</xsl:template>
	
	<!-- ============================================================================================= -->
	
	<xsl:template name="search-YELLOWFIN">
		<h1 align="left"><xsl:value-of select="/root/gui/harvesting/search"/></h1>
		
		<div id="yellowfin.searches"/>
		
		<button id="yellowfin.addSearch" class="content" onclick="harvesting.yellowfin.addSearchRow()">
			<xsl:value-of select="/root/gui/harvesting/add"/>
		</button>
	</xsl:template>

	<!-- ============================================================================================= -->
	
	<xsl:template name="options-YELLOWFIN">
		<h1 align="left"><xsl:value-of select="/root/gui/harvesting/options"/></h1>

		<table border="0">
			<!-- output schema - select from those that offer yellowfin
			     stylesheets -->
			<tr>
				<td class="padded" colspan="2">
					<xsl:value-of select="/root/gui/harvesting/yellowfinOutputSchema"/>
					&#160;
					<select id="yellowfin.outputSchema"/>
						
					<div id="yellowfinSchemaOptions" style="margin-left:40px;display:none;border-color:#f00;border-style:solid;border-width:1px">
						<table>
							<!-- optional stylesheet to apply to yellowfin output -->
							<tr>
								<td class="padded">
									<xsl:value-of select="/root/gui/harvesting/yellowfinStylesheet"/>
								</td><td class="padded">
									<select class="content" id="yellowfin.stylesheet" size="1"/>
								</td>
							</tr>

							<!-- template to copy yellowfin fragments into -->
							<tr>
								<td class="padded">
									<xsl:value-of select="/root/gui/harvesting/yellowfinTemplate"/>
								</td>
								<td class="padded">
									<select class="content" id="yellowfin.templateId" size="1"/>
								</td>
							</tr>
						</table>
					</div>
				</td>
			</tr>
		</table>

		<xsl:call-template name="schedule-widget">
			<xsl:with-param name="type">yellowfin</xsl:with-param>
		</xsl:call-template>
		</xsl:template>
	
	<!-- ============================================================================================= -->

	<xsl:template name="content-YELLOWFIN">
	<div style="display:none;"> <!-- UNUSED -->
		<h1 align="left"><xsl:value-of select="/root/gui/harvesting/content"/></h1>

		<table border="0">
			<tr>
				<td class="padded"><xsl:value-of select="/root/gui/harvesting/importxslt"/></td>
				<td class="padded">
					&#160;
					<select id="yellowfin.importxslt" class="content" name="importxslt" size="1"/>
				</td>
			</tr>

			<tr>
				<td class="padded"><xsl:value-of select="/root/gui/harvesting/validate"/></td>
				<td class="padded"><input id="yellowfin.validate" type="checkbox" value=""/></td>
			</tr>
		</table>
	</div>
	</xsl:template>

	<!-- ============================================================================================= -->

	<xsl:template name="privileges-YELLOWFIN">
		<h1 align="left"><xsl:value-of select="/root/gui/harvesting/privileges"/></h1>
		
		<table>
			<tr>
				<td class="padded" valign="top"><xsl:value-of select="/root/gui/harvesting/groups"/></td>
				<td class="padded"><select id="yellowfin.groups" class="content" size="8" multiple="on"/></td>					
				<td class="padded" valign="top">
					<div align="center">
						<button id="yellowfin.addGroups" class="content" onclick="harvesting.yellowfin.addGroupRow()">
							<xsl:value-of select="/root/gui/harvesting/add"/>
						</button>
					</div>
				</td>					
			</tr>
		</table>
		
		<table id="yellowfin.privileges">
			<tr>
				<th class="padded"><b><xsl:value-of select="/root/gui/harvesting/group"/></b></th>
				<th class="padded"><b><xsl:value-of select="/root/gui/harvesting/oper/op[@id='0']"/></b></th>
				<th class="padded"><b><xsl:value-of select="/root/gui/harvesting/oper/op[@id='5']"/></b></th>
				<th class="padded"><b><xsl:value-of select="/root/gui/harvesting/oper/op[@id='6']"/></b></th>
				<th/>
			</tr>
		</table>
		
	</xsl:template>
	
	<!-- ============================================================================================= -->

	<xsl:template name="categories-YELLOWFIN">
		<h1 align="left"><xsl:value-of select="/root/gui/harvesting/categories"/></h1>
		
		<select id="yellowfin.categories" class="content" size="8" multiple="on"/>
	</xsl:template>
	
	<!-- ============================================================================================= -->
	
    <xsl:template mode="selectoptions" match="day|hour|minute|dsopt">
		<option>
			<xsl:attribute name="value">
				<xsl:value-of select="."/>
			</xsl:attribute>
			<xsl:value-of select="@label"/>
		</option>
	</xsl:template>

    <!-- ============================================================================================= -->

</xsl:stylesheet>
