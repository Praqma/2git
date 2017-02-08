package examples

/* vim: set syntax=groovy:set et:set tabstop=4: */

def my_workspace = "c:/Users/cssr/git_conversion/ccm2git-main"
def my_workspace_file = new File(my_workspace)
if(!my_workspace_file.exists()) my_workspace_file.mkdirs()

def git_server = "http://dtdkcphlx0231.md-man.biz:7991/"

def ccm_addr_cli = "DTDKCPHNB124554:51501:100.64.0.2"

def this_project = "ems_bus~1_20131002"


source('ccm') {
    workspace "${my_workspace}/ccm_wa"
    revision this_project
    ccm_addr ccm_addr_cli
}

target('git', repository_name) {
    workspace "${my_workspace}/repos/ems_bus"
    user 'cssr'
    email 'claus.schneider-ext@man-eu.com'
    remote "ssh://git@$git_server/ems_bus.git"
    longPaths true
}

migrate {
    filters {
        filter {
//            criteria {
//
//            }
            extractions {
                baselineProperties()
            }
            actions {
                custom { project ->
                    println project.snapshot
                    println project.snapshotName
                    println project.snapshotRevision
                    println project.baselineRevision
                }

                // Scrub Git repository, so file deletions will also be committed
                cmd 'git reset --hard $baselineRevision', target.workspace

                custom {
                    println "Removing files except .git folder in: $target.workspace"
                    new File(target.workspace).eachFile { file ->
                        if(!file.name.startsWith(".git")) file.delete()
                    }
                    new File(target.workspace).eachFile { file ->
                        if(!file.name.startsWith(".git")) println file.getName()
                    }                }

                // Copy checkout into Git repository
                copy(source.workspace + "/code/" + snapshot + "/$snapshotName", target.workspace)

                custom {
                    println "Files in: $target.workspace"
                    new File(target.workspace).eachFile { file ->
                        if(!file.name.startsWith(".git")) println file.getName()
                    }
                }

                // Commit everything
                cmd 'git add -A .', target.workspace

                cmd 'git commit --allow-empty -m "$snapshotRevision"', target.workspace

                cmd 'git tag -f -m "$snapshotRevision" $snapshotRevision', target.workspace
            }
        }
    }
}
