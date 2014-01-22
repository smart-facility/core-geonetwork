<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:include href="edit.xsl"/>

	<!-- =================================================================== -->

	<xsl:template mode="script" match="/"/>

	<!-- =================================================================== -->
	<!-- page content -->
	<!-- =================================================================== -->

	<xsl:template name="content">
		<xsl:call-template name="formLayout">
			<xsl:with-param name="title" select="/root/gui/strings/taxonSearchTitle"/>
			<xsl:with-param name="content">
				<xsl:call-template name="form"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- =================================================================== -->

	<xsl:template name="form">
		<form id='taxonSearchForm'>
			<p>This form searches IBIS TaxonName data in the repository. All elements, aside from Year and Rank, are lucene text searches. The lucene query syntax is described <a href="http://lucene.apache.org/java/2_4_0/queryparsersyntax.html">here</a>.</p>
      <p> Try to limit your searches to unusual terms. A search on the name string "common wombat" will take longer and return more matches than "wombat" alone.</p>
      <table class="md" width="100%">
      	<tr>
        	<th class="md">Nomenclatural code ( <tt>nc</tt> )</th>
          <td class="padded">
          	<select class="md" name="nc">
            	<option value="b">Botanical</option>
              <option value="z">Zoological</option>
              <option value="" selected="true"/>
            </select>
					</td>
				</tr>
       	<tr>
        	<th class="md">Name string ( <tt>n</tt> )</th>
          <td class="padded">
						<input value="" columns="50" type="text" name="n"/>
          </td>
        </tr>
        <tr>
        	<th class="md">Uninomal ( <tt>u</tt> )</th>
          <td class="padded">
          	<input value="" columns="50" type="text" name="u"/>
          </td>
        </tr>
        <tr>
        	<th class="md"> Genus ( <tt>g</tt> )</th>
          <td class="padded">
          	<input value="" columns="50" type="text" name="g"/>
          </td>
        </tr>
        <tr>
        	<th class="md">Infrageneric name ( <tt>sg</tt> )</th>
          <td class="padded">
          	<input value="" columns="50" type="text" name="sg"/>
          </td>
        </tr>
        <tr>
        	<th class="md">Specific epithet ( <tt>s</tt> )</th>
          <td class="padded">
          	<input value="" columns="50" type="text" name="s"/>
          </td>
        </tr>
        <tr>
        	<th class="md">Subspecific epithet ( <tt>ss</tt> )</th>
          <td class="padded">
          	<input value="" columns="50" type="text" name="ss"/>
          </td>
        </tr>
        <tr>
        	<th class="md">Authority ( <tt>a</tt> )</th>
          <td class="padded">
          	<input value="" columns="50" type="text" name="a"/>
          </td>
        </tr>
        <tr>
        	<th class="md">Year ( <tt>y</tt> )</th>
          <td class="padded">
          	<input value="" columns="50" type="text" name="y"/>
          </td>
        </tr>
        <tr>
        	<th class="md">Name complete ( <tt>nn</tt> )</th>
          <td class="padded">
          	<input value="" columns="50" type="text" name="nn"/>
          </td>
        </tr>
        <tr>
        	<th class="md">Rank abbr ( <tt>r</tt> )</th>
          <td class="padded">
          	<input value="" columns="50" type="text" name="r"/>
          </td>
        </tr>
        <tr>
        	<th class="md">Typification ( <tt>tp</tt> )</th>
          <td class="padded">
          	<input value="" columns="50" type="text" name="tp"/>
         	</td>
        </tr>
        <tr>
        	<th class="md">Max matches ( <tt>max-matches</tt> )</th>
          <td class="padded">
          	<select class="md" name="max-matches">
            	<option value=""/>
              <option value="1">1</option>
              <option value="5">5</option>
              <option value="10">10</option>
              <option value="20" selected="true">20</option>
              <option value="50">50</option>
            </select>
          </td>
        </tr>
        <tr>
        	<th class="md">Min score ( <tt>min-score</tt> )</th>
          <td class="padded">
          	<select class="md" name="min-score">
            	<option value="0">0</option>
              <option value="1" selected="true">1</option>
              <option value="2">2</option>
              <option value="10">10</option>
            </select>
          </td>
        </tr>
        <tr>
        	<td align="right" colspan="2">
          	<input value="Search" type="button" id="taxonSearchButton" onclick="submitTaxonSearch('{/root/request/ref}');"/>
					</td>
        </tr>
      </table>
      <input name="fmt" value="xml" type="hidden"/>
    </form>
		<br/>
		<div id="taxonSearchWaitMessage" style="position:absolute;
		                                   left:45%;top:45%;display:none;">
			<span id="taxonSearchWaitMessage">Waiting for results...</span>
			<img src="{/root/gui/url}/images/spinner.gif" alt="busy"/>
		</div>
		<div class="row" id="taxonSearchResultsHeader" style="display:none;">Search Results:</div>
		<div id="taxonSearchResults" class="table-container" style="display:none;">
			<!-- the results of the search - filled out by javascript 
			     submitTaxonSearch -->
		</div>
	</xsl:template>

	<!-- =================================================================== -->
	
</xsl:stylesheet>
