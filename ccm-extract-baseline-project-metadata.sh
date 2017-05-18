#!/usr/bin/env bash

ccm_project_name=$1
repo_convert_rev_tag=$2

test "${ccm_project_name}x" == "x"      && ( echo "'ccm_project_name' not set - exit"       && exit 1 )
test "${repo_convert_rev_tag}x" == "x"  && ( echo "'repo_convert_rev_tag' not set - exit"   && exit 1 )

ccm_baseline_obj_this=$(ccm query "has_project_in_baseline('${ccm_project_name}~$(echo ${repo_convert_rev_tag} | sed -e 's/xxx/ /g'):project:1') and release='$(ccm query "name='${ccm_project_name}' and version='$(echo ${repo_convert_rev_tag} | sed -e 's/xxx/ /g')' and type='project'" -u -f "%release")'" -u -f "%objectname" | head -1 )

if [ "${ccm_baseline_obj_this}X" != "X" ]; then
    echo > ./tag_meta_data.txt
    echo "---------------------------------------------------------" >> ./tag_meta_data.txt
    echo "Baseline information:"                                     >> ./tag_meta_data.txt
    echo "---------------------------------------------------------" >> ./tag_meta_data.txt
    ccm baseline -show info -v "${ccm_baseline_obj_this}"            >> ./tag_meta_data.txt
    echo >> ./tag_meta_data.txt

    echo >> ./tag_meta_data.txt
    echo "---------------------------------------------------------" >> ./tag_meta_data.txt
    echo "Project baseline:"                                         >> ./tag_meta_data.txt
    echo "---------------------------------------------------------" >> ./tag_meta_data.txt
    ccm query "is_baseline_project_of('${ccm_project_name}~$(echo ${repo_convert_rev_tag} | sed -e 's/xxx/ /g'):project:1')" -f "%displayname" >> ./tag_meta_data.txt || echo "  <none>" >> ./tag_meta_data.txt
    echo >> ./tag_meta_data.txt

    echo >> ./tag_meta_data.txt
    echo "---------------------------------------------------------">> ./tag_meta_data.txt
    echo "Master Change Requests (MCR):"                            >> ./tag_meta_data.txt
    echo "---------------------------------------------------------">> ./tag_meta_data.txt
    ccm query "has_associatedImpl(has_associated_task(is_dirty_task_in_baseline_of('${ccm_baseline_obj_this}')))" -u -f "%displayname %resolver %release %problem_synopsis" >> ./tag_meta_data.txt || echo "<none>" >> ./tag_meta_data.txt
    echo >> ./tag_meta_data.txt

    echo >> ./tag_meta_data.txt
    echo "---------------------------------------------------------" >> ./tag_meta_data.txt
    echo "Fully integrated Implementation Change Requests(ICR):"     >> ./tag_meta_data.txt
    echo "---------------------------------------------------------" >> ./tag_meta_data.txt
    ccm baseline -show fully_included_change_requests -groupby "Release: %release"  -f "%displayname %resolver %release %problem_synopsis" "${ccm_baseline_obj_this}" >> ./tag_meta_data.txt  || echo "<none>" >> ./tag_meta_data.txt
    echo >> ./tag_meta_data.txt

    echo >> ./tag_meta_data.txt
    echo "---------------------------------------------------------" >> ./tag_meta_data.txt
    echo "Partially integrated Implementation Change Requests(ICR):" >> ./tag_meta_data.txt
    echo "---------------------------------------------------------" >> ./tag_meta_data.txt
    ccm baseline -show fully_included_change_requests -groupby "Release: %release" -f "%displayname %resolver %release %problem_synopsis" "${ccm_baseline_obj_this}" >> ./tag_meta_data.txt  || echo "<none>" >> ./tag_meta_data.txt
    echo >> ./tag_meta_data.txt

    echo >> ./tag_meta_data.txt
    echo "---------------------------------------------------------" >> ./tag_meta_data.txt
    echo "Tasks integrated in baseline:"                             >> ./tag_meta_data.txt
    echo "---------------------------------------------------------" >> ./tag_meta_data.txt
    ccm baseline -show tasks -f "%displayname %{create_time[dateformat='yyyy-M-dd HH:MM:SS']} %resolver %status %release %task_synopsis" "${ccm_baseline_obj_this}" >> ./tag_meta_data.txt || echo "<none>" >> ./tag_meta_data.txt
    echo >> ./tag_meta_data.txt

    echo >> ./tag_meta_data.txt
    echo "---------------------------------------------------------" >> ./tag_meta_data.txt
    echo "Tasks integrated in baseline (verbosed):"                  >> ./tag_meta_data.txt
    echo "---------------------------------------------------------" >> ./tag_meta_data.txt
    ccm task -sh info -v @ >> ./tag_meta_data.txt || echo "<none>"   >> ./tag_meta_data.txt
    echo >> ./tag_meta_data.txt

else
    echo > ./tag_meta_data.txt
    echo "---------------------------------------------------------">> ./tag_meta_data.txt
    echo "NO BASELINE OBJECT ASSOCIATED WITH THIS PROJECT REVISION" >> ./tag_meta_data.txt
    echo "---------------------------------------------------------">> ./tag_meta_data.txt
    echo >> ./tag_meta_data.txt

    echo >> ./tag_meta_data.txt
    echo "---------------------------------------------------------" >> ./tag_meta_data.txt
    echo "Project baseline:"                                         >> ./tag_meta_data.txt
    echo "---------------------------------------------------------" >> ./tag_meta_data.txt
    ccm query "is_baseline_project_of('${ccm_project_name}~$(echo ${repo_convert_rev_tag} | sed -e 's/xxx/ /g'):project:1')" -f "%displayname"  >> ./tag_meta_data.txt || echo "  <none>" >> ./tag_meta_data.txt
    echo >> ./tag_meta_data.txt

    echo >> ./tag_meta_data.txt
    echo "---------------------------------------------------------" >> ./tag_meta_data.txt
    echo "Master Change Requests (MCR):"                             >> ./tag_meta_data.txt
    echo "---------------------------------------------------------" >> ./tag_meta_data.txt
    ccm query "has_associatedImpl(has_associated_task(is_task_in_folder_of(is_folder_in_rp_of('${ccm_project_name}~$(echo ${repo_convert_rev_tag} | sed -e 's/xxx/ /g'):project:1'))))" -f "%displayname %resolver %release %problem_synopsis" >> ./tag_meta_data.txt  || echo "<none>" >> ./tag_meta_data.txt
    echo >> ./tag_meta_data.txt

    echo "---------------------------------------------------------" >> ./tag_meta_data.txt
    echo "Related/Integrated Implementation Change Requests(ICR):"   >> ./tag_meta_data.txt
    echo "---------------------------------------------------------" >> ./tag_meta_data.txt
    ccm query "has_associated_task(is_task_in_folder_of(is_folder_in_rp_of('${ccm_project_name}~$(echo ${repo_convert_rev_tag} | sed -e 's/xxx/ /g'):project:1')))" -f "%displayname %resolver %release %problem_synopsis"  >> ./tag_meta_data.txt  || echo "<none>" >> ./tag_meta_data.txt
    echo >> ./tag_meta_data.txt

    echo "---------------------------------------------------------" >> ./tag_meta_data.txt
    echo "Tasks integrated in project:"                              >> ./tag_meta_data.txt
    echo "---------------------------------------------------------" >> ./tag_meta_data.txt
    ccm query "is_task_in_folder_of(is_folder_in_rp_of('${ccm_project_name}~$(echo ${repo_convert_rev_tag} | sed -e 's/xxx/ /g'):project:1'))" -f "%displayname %{create_time[dateformat='yyyy-M-dd HH:MM:SS']} %resolver %status %release %task_synopsis"  >> ./tag_meta_data.txt || echo "<none>" >> ./tag_meta_data.txt
    echo >> ./tag_meta_data.txt

    echo >> ./tag_meta_data.txt
    echo "---------------------------------------------------------" >> ./tag_meta_data.txt
    echo "Tasks integrated in project (verbosed):"                   >> ./tag_meta_data.txt
    echo "---------------------------------------------------------" >> ./tag_meta_data.txt
    ccm task -sh info -v @ >> ./tag_meta_data.txt || echo "<none>"   >> ./tag_meta_data.txt
    echo >> ./tag_meta_data.txt
fi
cat ./tag_meta_data.txt
rm -f ./tag_meta_data.txt