package examples

source('clearcase') {
    workspace "${jenkins_workspace}/view"
    configSpec "${jenkins_workspace}/spec.cs"
}

def repos = ['client', 'server', 'utils']
repos.each {
    target('git', it) {
        workspace "${jenkins_workspace}/repos/$it"
        user 'bosse'
        email 'bo@consultant.volvo.com'
    }
}

migrate {
    filters {
        filter {
            criteria {
                afterLabel 'gtag4_2016W37_3'
            }
            extractions {
                label('myLabel')
            }
            actions {
                // Whatever copying of whatever directories
                // to whatever repos we want to commit them
                // to.
                repos.each {
                    cmd 'git add .', "$it".workspace
                    cmd 'git commit --allow-empty -m"$myLabel"', "$it".workspace
                    cmd "git tag $myLabel", "$it".workspace
                }
            }
        }
    }
}