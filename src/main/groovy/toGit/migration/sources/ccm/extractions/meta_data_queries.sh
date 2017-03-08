#!/usr/bin/env bash

project="bes2~BES-HMI-SW-0906-6.2:project:1"
baseline_obj=`ccm query "has_project_in_baseline(\'${project}\')" -u -f "%objectname"`

for task in `ccm query "is_task_in_folder_of('6680~1:folder:probtrac')" -u -f "%displayname"` ; do
    ccm task -show info $task -u -f "\
%displayname \
----------------
%resolver
%release
%completion_date


    echo
    echo
    echo "Objects:"
    echo "---------------"
    ccm task -show objects $task -u -f "%objectname %status %owner"
done