package examples

/* vim: set syntax=groovy:set et:set tabstop=4: */

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

def ccm_home_cli
if ( !System.getenv("CCM_HOME") ){
    println "CCM_HOME variable not set"
    System.exit(1)
} else {
    ccm_home_cli = System.getenv("CCM_HOME")
}
def system_path2 = System.getenv("PATH")

def my_workspace
if ( !my_workspace_root ) {
    my_workspace_root = "/data/Synergy/ccm2git-main/" 
    my_workspace = my_workspace_root + "/" + ccm_project + "/"
} else {
    my_workspace = my_workspace_root + "/" + ccm_project + "/"
}

def git_server_path_this
if ( !git_server_path ){
    git_server_path_this = "dtdkcphlx0231.md-man.biz:7991/scarp"
} else {
    git_server_path_this = git_server_path
}



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
    user 'Claus Schneider(Praqma)'
    email 'claus.schneider-ext@man.eu'
    remote "ssh://git@${git_server_path_this}/${ccm_project}.git"
    longPaths true
    ignore ""
}

migrate {
    filters {
        filter {
            criteria {
                AlreadyConverted(target.workspace)
            }
            extractions {
                baselineProperties(source.workspace)
            }
            actions {

                // Scrub Git repository, so file deletions will also be committed
                cmd 'git reset --hard $baselineRevision_wstatus', target.workspace

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

                custom { project ->
                    def sout = new StringBuilder(), serr = new StringBuilder()
                    def cmd_line = 'git commit -m "' + project.snapshotRevision + '"'
                    println cmd_line

                    def envVars = System.getenv().collect { k, v -> "$k=$v" }
                    envVars.add('GIT_COMMITTER_DATE=' + project.snapshot_commiter_date)
                    envVars.add('GIT_AUTHOR_DATE=' + project.snapshot_commiter_date)
                    def cmd = cmd_line.execute(envVars, new File(target.workspace))
                    cmd.waitForProcessOutput(sout, serr)
                    def exitValue = cmd.exitValue()
                    println "Standard out:"
                    println "'" + sout + "'"
                    println "Standard error:"
                    println "'" + serr + "'"
                    println "Exit code: " + exitValue

                    if ( sout.contains('nothing to commit, working directory clean') )
                        println "Empty workspace - skip, but still tag"

                    if (exitValue) {
                        if ( sout.contains('nothing to commit, working directory clean') )
                            println "Empty workspace - skip, but still tag"
                        else
                            throw new Exception(cmd_line + ": gave exit code: $exitValue")
                    }
                    if (serr.toString().readLines().size() > 0) {
                        throw new Exception(cmd_line + ": standard error contains text lines: " + serr.toString().readLines().size())
                    }
                }

                // The file for tag info is generated during MetaDataExtraction
                custom { project ->
                    new File(target.workspace + File.separator + "tag_meta_data.txt").withWriter { out ->
                        project.baseline_info.each {
                            out.println it
                        }
                    }
                }
                custom { project ->
                    def sout = new StringBuilder(), serr = new StringBuilder()
                    def cmd_line = "git tag -F tag_meta_data.txt " + project.snapshotRevision + "_" + project.snapshot_status
                    println cmd_line

                    def envVars = System.getenv().collect { k, v -> "$k=$v" }
                    envVars.add('GIT_COMMITTER_DATE=' + project.snapshot_commiter_date)
                    envVars.add('GIT_AUTHOR_DATE=' + project.snapshot_commiter_date)
                    def cmd = cmd_line.execute(envVars,new File(target.workspace))
                    cmd.waitForProcessOutput(sout, serr)
                    def exitValue = cmd.exitValue()
                    println "Standard out:"
                    println "'" + sout + "'"
                    println "Standard error:"
                    println "'" + serr + "'"
                    println "Exit code: " + exitValue
                    if ( exitValue ){
                        throw new Exception(cmd_line + ": gave exit code: $exitValue" )
                    }
                    if ( serr.toString().readLines().size() > 0 ){
                        throw new Exception(cmd_line + ": standard error contains text lines: " + serr.toString().readLines().size() )
                    }
                }

                cmd 'du -sh .git >> ../git_sizes.txt', target.workspace
                cmd 'tail -1 ../git_sizes.txt', target.workspace
            }
        }
    }
}
