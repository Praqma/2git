#!/usr/bin/env bash

# Load functions
source ./_ccm-functions.sh || source ${BASH_SOURCE%/*}/_ccm-functions.sh

[[ "${debug:-}" == "true" ]] && set -x


set -euo pipefail

ccm_project_name="<space> <space>"
expected_result="<space>-<space>"
result=""
printf "%-8s: %-80s : %-75s " "test" "byref_translate_from_ccm_name2git_repo" "${ccm_project_name} -> $expected_result"
byref_translate_from_ccm_name2git_repo "$ccm_project_name" result
[[ "$result" == "${expected_result}" ]] && { printf "%10s\n" "SUCCESS" ; }|| { printf " FAILED: ${expected_result} != $result\n" ;}

ccm_project_name="<hyphan>-<hyphan>"
expected_result="<hyphan>?<hyphan>"
result=""
printf "%-8s: %-80s : %-75s " "test" "byref_translate_from_git_repo2ccm_name_query" "${ccm_project_name} -> $expected_result"
byref_translate_from_git_repo2ccm_name_query $ccm_project_name result
[[ "$result" == "${expected_result}" ]] && { printf "%10s\n" "SUCCESS" ; }|| { printf " FAILED: ${expected_result} != $result\n" ;}

ccm_query_name_instance_string="<question>?<question>"
ccm_query_instance="1"
expected_result="<question> <question>"
result=""
printf "%-8s: %-80s : %-75s " "test" "byref_translate_from_ccm_name_instance_query2ccm_name" "${ccm_query_name_instance_string} -> $expected_result"
byref_translate_from_ccm_name_instance_query2ccm_name $ccm_query_name_instance_string $ccm_query_instance result
[[ "$result" == "${expected_result}" ]] && { printf "%10s\n" "SUCCESS" ; }|| { printf " FAILED: ${expected_result} != $result\n" ;}

git_repo_name="<hyphan>-<hyphan>"
ccm_query_instance="1"
expected_result="<hyphan> <hyphan>"
result=""
printf "%-8s: %-80s : %-75s " "test" "byref_translate_from_git_repo2ccm_name" "${ccm_project_name} -> $expected_result"
byref_translate_from_git_repo2ccm_name $git_repo_name $ccm_query_instance result
[[ "$result" == "${expected_result}" ]] && { printf "%10s\n" "SUCCESS" ; }|| { printf " FAILED: ${expected_result} != $result\n" ;}

git_repo_4part="<hyphan>-<hyphan>~<:project:1"
expected_result="<hyphan> <hyphan>~<version>:project:1"
result=""
printf "%-8s: %-80s : %-75s " "test" "byref_translate_from_git_repo_4part2ccm_4part" "${git_repo_4part} -> $expected_result"
byref_translate_from_git_repo_4part2ccm_4part "$git_repo_4part" result
[[ "$result" == "${expected_result}" ]] && { printf "%10s\n" "SUCCESS" ; }|| { printf " FAILED: ${expected_result} != $result\n" ;}

ccm_4part="<project-name>~<space> <space>:project:1"
expected_result="<project-name>~<space>-<space>:project:1"
result=""
printf "%-8s: %-80s : %-75s " "test" "byref_translate_from_ccm_4part2git_repo_4part" "${ccm_4part} -> $expected_result"
byref_translate_from_ccm_4part2git_repo_4part "${ccm_4part}" result
[[ "$result" == "${expected_result}" ]] && { printf "%10s\n" "SUCCESS" ; }|| { printf " FAILED: ${expected_result} != $result\n" ;}

