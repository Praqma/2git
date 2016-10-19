package examples

source('clearcase') {
    workspace "${jenkins_workspace}/view"
    configSpec "${jenkins_workspace}/spec.cs"
    labelVob "arbitrary_vob_name"
}

def repos = ['client', 'server', 'utils']
repos.each { repo ->
    target('git', repo) {
        workspace "${jenkins_workspace}/repos/$repo"
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

                // Copy things around here...

                repos.each { repo ->
                    cmd 'git add .', targets[repo].workspace
                    cmd 'git commit --allow-empty -m"$myLabel"', targets[repo].workspace
                    cmd 'git tag $myLabel', targets[repo].workspace
                }
            }
        }
    }
}