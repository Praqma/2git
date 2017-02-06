package examples

/* vim: set syntax=groovy:set et:set tabstop=4: */

def my_workspace = "ccm2git-main"
def my_workspace_file = new File(my_workspace)
if(!my_workspace_file.exists()) my_workspace_file.mkdirs()

def git_server = "http://dtdkcphlx0231.md-man.biz:7991/"

def this_project = "ems_bus~1_20131002"

source('ccm') {
    workspace my_workspace
    revision this_project
}

target('git', repository_name) {
    workspace "${workspace}/repos/$repository_name"
    user 'cssr'
    email 'claus.schneider-ext@man-eu.com'
    remote "ssh://git@$git_server/${repository_name}.git"
    longPaths true
}

migrate {
    filters {
        filter {
//            criteria {
//
//            }
//            extractions {
//                label('label_key')
//            }
            actions {
                // Scrub Git repository, so file deletions will also be committed
                new File(target.workspace).eachFile { file ->
                    if(!file.name.startsWith(".git")) file.delete()
                }

                // Copy ClearCase view into Git repository
                copy(source.workspace, target.workspace)

                // Commit everything
                cmd 'git add .', target.workspace
                cmd 'git commit -m "$myBaselineName"', target.workspace
            }
        }
    }
}
