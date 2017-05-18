#!/bin/bash
#set -x # får shell til at displaye kommandoer

export PATH="/c/Program Files (x86)/IBM/Rational/Synergy/7.2.1/bin:${PATH}"

BASELINE_PROJECT=$1
UNTIL_PROJECT=$2

if [ "yes" == "yes" ]; then
until [ "${BASELINE_PROJECT}X" == "X" -o "$BASELINE_PROJECT" == "$UNTIL_PROJECT" ] ; do
#	ccm rp -sh folders $BASELINE_PROJECT -u -f "%displayname" >> $OUTPUTFILE
    BASELINE_PROJECT=`printf "${BASELINE_PROJECT}" | sed -e 's/xxx/ /g' `
	query="is_baseline_project_of('${BASELINE_PROJECT}:project:1')"
	BASELINE_PROJECT=`ccm query "is_baseline_project_of('${BASELINE_PROJECT}:project:1')" -u -f "%name~%version" | sed -e 's/ /xxx/g'` 
	bl_print=`printf "${BASELINE_PROJECT}" | awk -F"~" '{print $2}'`
	if [ "${BASELINE_PROJECT}X" != "X" ] ; then
		printf "${BASELINE_PROJECT} -> "
	fi
done
printf "void\n"
printf "$BASELINE_PROJECT"
exit 0
fi
