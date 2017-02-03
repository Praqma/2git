package examples

/* vim: set syntax=groovy:set et:set tabstop=4: */

def jenkins_workspace = "cc2git-main"
def jenkins_workspace_file = new File(jenkins_workspace)
if(!jenkins_workspace_file.exists()) jenkins_workspace_file.mkdirs()

def clearcase_workspace = "$jenkins_workspace/view"
def clearcase_view_tag = "2git-${UUID.randomUUID()}"

def config_spec = "$jenkins_workspace/configspec.cs"
def config_spec_file = new File(config_spec)
if(!config_spec_file.exists()) config_spec_file.createNewFile()

def git_server = "bitbucket.company.com:8081"
def repository_name = 'gtag4-main-vob'
def vobs = [
        'ArbitraryArmadillo_src',
        'ArbitraryArmadillo_test',
        'ConspicuousCrocodile_src',
        'ConspicuousCrocodile_test'
]

source('clearcase') {
    workspace clearcase_workspace
    configSpec config_spec
    labelVob vobs[0]
    vobPaths vobs
    viewTag clearcase_view_tag
}

target('git', repository_name) {
    workspace "${jenkins_workspace}/repos/$repository_name"
    user 'migration-user'
    email 'migration@company.com'
    remote "ssh://git@$git_server/${repository_name}.git"
    longPaths true
}

migrate {
    filters {
        filter {
            criteria {
                labelName("(\\d{6})-(.+)")
                afterLabel("010116-v2.1.3")
            }
            extractions {
                label('label_key')
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
            }
        }
    }
}
