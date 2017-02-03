package examples
/*
 The following setup allows you to migrate baselines starting from where you last left off.
 We do this by adding the baseline selector as a note to the git commit.
 Whenever a migration starts, we grab that selector and only migrate baselines that are newer than that baseline.
 Note that this approach assumes a read-only git repository,
 otherwise you're better off using tags and fetching the repo before migrating.
*/

source('ccucm') {
    component "myComp@\\vob"
    stream "myStream@\\vob"
}

target('git') {
    user 'johan'
    email 'joa@praqma.net'
}

migrate {
    filters {
        filter {
            criteria {
                def lastBaselineName = "git --git-dir $target.workspace notes show HEAD".execute().text
                if (lastBaselineName) {
                    println "Picking up migration from $lastBaselineName."
                    afterBaseline lastBaselineName
                } else {
                    println "No previous baseline found."
                    afterDate 'dd-MM-yyy', '01-05-2016'
                }
            }
            extractions {
                baselineProperty([myBaselineName: 'fqname'])
            }
            actions {
                // Scrub Git repository, so file deletions will also be committed
                custom {
                    new File(target.workspace).eachFile { file ->
                        if(!file.name.startsWith(".git")) file.delete()
                    }
                }

                // Copy ClearCase view into Git repository
                copy(source.workspace, target.workspace)

                // Commit everything
                cmd 'git add .', target.workspace
                cmd 'git commit -m "$myBaselineName"', target.workspace

                // Mark this commit with a note
                cmd 'git notes add -m"$myBaselineName" HEAD'
            }
        }
    }
}