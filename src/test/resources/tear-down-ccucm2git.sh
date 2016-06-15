#!/usr/bin/env bash

# Delete test vob
reg="(\\\\\\\\.*\\.vbs)"
path=$(cleartool lsvob \\ccucm2git)
if [[ $path =~ $reg ]]; then cleartool rmvob -force ${BASH_REMATCH[1]}; fi
