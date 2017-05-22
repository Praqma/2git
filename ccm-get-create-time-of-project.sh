#!/usr/bin/env bash

ccm_project_name=$1
repo_convert_rev_tag=$2

test "${ccm_project_name}x" == "x"      && ( echo "'ccm_project_name' not set - exit"       && exit 1 )
test "${repo_convert_rev_tag}x" == "x"  && ( echo "'repo_convert_rev_tag' not set - exit"   && exit 1 )

ccm properties -f "%create_time" "${ccm_project_name}~$(echo ${repo_convert_rev_tag} | sed -e 's/xxx/ /g'):project:1"
