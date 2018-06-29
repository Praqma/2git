---
layout:            2git
organization:      Praqma
repo:              cc2git
github-issues:     true
javadoc:           false
---

## Quickstart

The following guide explains how to quickly get up-and-running with 2git.
It showcases a simple migration from ClearCase UCM to Git.

### Get 2git

First, you'll need to get your hands on 2git itself.
Fetch the latest release from [GitHub](https://github.com/Praqma/2git/releases) or build them yourself using `gradle`.

### Write your first migration script

2git interprets DSL scripts to execute its migrations.
Below is a simple script to migrate a ClearCase UCM component to a git repository.

```groovy
/* Define and configure our source VCS, in this case, ClearCase UCM */
source('ccucm') {
    workspace "migration/ccucm"

    component 'component:RedBlox@\\bloxVob'
    stream 'stream:RedBlox_Int@\\bloxVob'
}

/* Define and configure our target VCS, in this case, Git */
target('git') {
    workspace "migration/git"
}

/* Define the migration process and steps */
migrate {
    filters {
        /* For the ClearCase UCM Baselines... */
        filter {
            /* that match the following criteria: */
            criteria {
                /* Created since May, 2016 */
                afterDate 'dd-MM-yyy', '01-05-2016'
            }
            /* we extract the following information from the environment */
            extractions {
                /* The Baseline name */
                baselineProperty([myBaselineName: 'shortname'])
            }
            /* and execute the following actions: */
            actions {
                /* Copy the contents of the source workspace to the target workspace */
                copy(source.workspace, target.workspace)
                /* Commit the contents of the target workspace */
                cmd 'git add .', target.workspace
                cmd 'git commit -m "$myBaselineName"', target.workspace
            }
        }
    }
}

```

### Run your first migration

Now run 2git, supplying it with your migration script.

```shell
./2git path/to/myScript.groovy
```
