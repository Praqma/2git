package examples

/* vim: set syntax=groovy:set et:set tabstop=4: */

//def ccm_project = "make2"
//def start_project = ccm_project + "~makedev2_migr"
def ccm_delimiter='~'

def ccm_project
if ( !start_project?.trim()) {
    println "start_project not set"
    System.exit(1)
} else {
    if ( !start_project.contains(ccm_delimiter) ){
        println "start_project does not contain a ${ccm_delimiter}\n" +
                "Provide the start_project=<projectname>~<revision>"
        System.exit(1)
    } else {
        ccm_project = start_project.split(ccm_delimiter)[0]
        ccm_revision = start_project.split(ccm_delimiter)[1]

        if ( !ccm_revision || start_project.contains(':') ) {
            println "start_project does contains ':' \n" +
                    "Provide the start_project=<projectname>~<revision>"
            System.exit(1)
        }
    }
    if ( ! ccm_project ) {
        println "Could not extract ccm_project name from start_project"
        System.exit(1)
    }
}

def ccm_addr_cli
if ( !System.getenv("CCM_ADDR") ){
    println "CCM_ADDR system variable not set. Please start session prior to calling the ccm2git \n \
( ex: ccm start -m -d /data/ccmdb/<db> -s http://<server>:<port> -q ) "
    System.exit(1)
} else {
    ccm_addr_cli = System.getenv("CCM_ADDR")
}
// "DTDKCPHPW111426:61516:10.100.104.126"

def ccm_home_cli
if ( !System.getenv("CCM_HOME") ){
    println "CCM_HOME variable not set"
    System.exit(1)
} else {
    ccm_home_cli = System.getenv("CCM_HOME")
}
def system_path2 = System.getenv("PATH")

//def my_workspace = "c:/Users/cssr/git_conversion/ccm2git-main/" + ccm_project
def my_workspace

if ( !my_workspace_root ) {
    my_workspace_root = "/data/Synergy/ccm2git-main/" 
    my_workspace = my_workspace_root + ccm_project + "/"
} else {
    my_workspace = my_workspace_root + ccm_project + "/"
}

def git_server = "http://dtdkcphlx0231.md-man.biz:7991/scarp"


def my_workspace_file = new File(my_workspace)
if(!my_workspace_file.exists()) my_workspace_file.mkdirs()
my_workspace_file = new File(my_workspace + "/ccm_wa")
if(!my_workspace_file.exists()) my_workspace_file.mkdirs()


source('ccm') {
    workspace "${my_workspace}/ccm_wa"
    revision start_project
    ccm_addr ccm_addr_cli
    ccm_home ccm_home_cli
    system_path system_path2
}

target('git', repository_name) {
    workspace "${my_workspace}/repo/" + ccm_project
    user 'cssr'
    email 'claus.schneider-ext@man-eu.com'
    remote "ssh://git@$git_server/${ccm_project}.git"
    longPaths true
}

migrate {
    filters {
        filter {
            criteria {
                AlreadyConverted(target.workspace)
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

                cmd 'du -sh .git >> ../git_sizes.txt', target.workspace
                cmd 'du -sh .git', target.workspace
            }
        }
    }
}
