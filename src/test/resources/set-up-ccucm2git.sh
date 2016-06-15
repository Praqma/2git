#!/usr/bin/env bash
component=MyComponent@\\ccucm2git

# Create VOB
cleartool mkvob -tag \\ccucm2git -ucmproject -c "ccucm2git test environment" -stgloc -auto

# Create components
cleartool mkcomp -c "ccucm2git test component" -nroot ${component}
