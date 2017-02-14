package examples

/* vim: set syntax=groovy:set et:set tabstop=4: */

def ccm_project = "bes2"
def start_project = $ccm_project + "~1.55_besdev2_0506.7_"

def ccm_addr_cli = "DTDKCPHNB124554:53331:100.64.0.2"

def my_workspace = "c:/Users/cssr/git_conversion/ccm2git-main/" + ccm_project

def git_server = "http://dtdkcphlx0231.md-man.biz:7991/"


def my_workspace_file = new File(my_workspace)
if(!my_workspace_file.exists()) my_workspace_file.mkdirs()
my_workspace_file = new File(my_workspace + "/ccm_wa")
if(!my_workspace_file.exists()) my_workspace_file.mkdirs()

source('ccm') {
    workspace "${my_workspace}/ccm_wa"
    revision start_project
    ccm_addr ccm_addr_cli
}

target('git', repository_name) {
    workspace "${my_workspace}/repo/" + ccm_project
    user 'cssr'
    email 'claus.schneider-ext@man-eu.com'
    remote "ssh://git@$git_server/bes2.git"
    longPaths true
}

migrate {
    filters {
        filter {
            criteria {
                AlreadyConverted()
            }
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
                        if(!file.name.startsWith(".git")) {
                            if (!file.isDirectory()) {
                                println file.getName()
                                file.delete()
                            } else {
                                println file.getName()
                                file.deleteDir()
                            }
                        }
                    }
                    println "Remaining files except .git folder in: $target.workspace"
                    new File(target.workspace).eachFile { file ->
                        if(!file.name.startsWith(".git")) println file.getName()
                    }
                }

                // Copy checked out into Git repository
                copy("$source.workspace/code/\$snapshot/\$snapshotName", target.workspace)

                custom {
                    println "First level files in: $target.workspace"
                    new File(target.workspace).eachFile { file ->
                        if(!file.name.startsWith(".git")) println file.getName()
                    }
                }

                // Commit everything
                cmd 'git add -A .', target.workspace

                cmd 'git commit -m "$snapshotRevision" || (echo "Empty commit.." && exit 0)', target.workspace

//                cmd 'git commit --allow-empty -m "$snapshotRevision"', target.workspace

                cmd 'git tag -f -m "$snapshotRevision" "$snapshotRevision"', target.workspace
            }
        }
    }
}
