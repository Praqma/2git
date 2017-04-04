#!/usr/bin/env bash
#set -x
project="bes2~BES-HMI-SW-0906-6.2:project:1"
baseline_obj=`ccm query "has_project_in_baseline('${project}')" -u -f "%objectname"`

#ccm task -show info -v 17812

for task in $(ccm query "is_task_in_folder_of(is_folder_in_rp_of('${project}'))" -u -f "%displayname" | sed -e 's/ //g') ; do
    ccm task -show info $task -f "\n \
%displayname \n \
---------------------------------------- \
Resolver:        %resolver \
Release:         %release \
Completion date: %complete_date \
Task description: \
%task_description"
    echo
    echo "Objects:"
    echo "-------------------------------------"
    ccm task -show objects $task -u -f "%objectname %owner"
done