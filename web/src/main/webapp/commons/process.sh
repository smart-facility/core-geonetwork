#!/bin/sh
# Create the creative commons jurisidictions and licenses
# Create the data commons stuff by copying
for adir in /usr/local/src/geonetwork.trunk/web/src/main/webapp/loc/*
do
	dir=../loc/`basename $adir`
	echo Doing directory $dir

	locale=`basename $adir`

  # Do creativecommons
	mkdir -p $dir/xml/creativecommons

	# create jurisdictions html select block (jurisdiction.html)
	wget http://api.creativecommons.org/rest/1.5/support/jurisdictions?select=f_jurisdiction\&locale=$locale -O junk
	cat junk | sed 's/\&/&amp;/g' > $dir/xml/creativecommons/jurisdictionSelectOptions

	# for each jurisdiction in the select block, get licenses and create 
	# a file that holds all licenses for the jurisdiction (jurisdictions.xml)
	java -jar saxon8.jar -s $dir/xml/creativecommons/jurisdictionSelectOptions -o jurisdictions extractjuris.xsl
	echo "<jurisdictions>" > $dir/xml/creativecommons/jurisdictionLicenses.xml
	export IFS=#
	while read name juris
	do
		wget http://api.creativecommons.org/rest/1.5/simple/chooser?select=f_choice\&jurisdiction=$juris\&locale=$locale -O junk 
		echo "<jurisdiction name=\"$juris\">" >> $dir/xml/creativecommons/jurisdictionLicenses.xml
		cat junk >> $dir/xml/creativecommons/jurisdictionLicenses.xml
		echo "</jurisdiction>" >> $dir/xml/creativecommons/jurisdictionLicenses.xml
		rm -f junk
	done < jurisdictions
	echo "</jurisdictions>" >> $dir/xml/creativecommons/jurisdictionLicenses.xml
	rm -f junk jurisdictions

done
exit 0

