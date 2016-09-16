package examples
/*
 The following setup allows you to migrate baselines starting from where you last left off.
 This is useful when you plan on keeping both VCS's alive during a bigger migration project.

 We do this by adding the baseline selector as a note to the git commit.
 Whenever a migration starts, we grab that selector and only migrate baselines that are newer than that baseline.
 Note that this approach assumes a read-only git repository.
*/


def tempDir = "c:/tmp/2git"

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
                copy(source.workspace, tempDir)
                flattenDir(tempDir, 3)
                copy(tempDir, target.workspace)
                emptyDir(tempDir)

                cmd 'git add .', target.workspace
                cmd 'git commit -m "commit for: $myBaselineName"', target.workspace
                git 'notes add -m"$myBaselineName" HEAD'
            }
        }
    }
}