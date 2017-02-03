package examples

source('ccucm') {
    component "_Client@\\2Cool_PVOB"
    stream "Client_int\\2Cool_PVOB"
}

target('git', 'git') {
    user 'johan'
    email 'joa@praqma.net'
}

target('artifactory', 'art') {
    url 'http://artifactory.blumanufacturing.com:8081/artifactory/api'
    repository 'libs-snapshot-local'
    user 'art-user'
    password 'mYSecrEt2000'
}

migrate {
    filters {
        filter {
            criteria {
                afterDate 'dd-MM-yyy', '01-01-2015'
            }
            extractions {
                baselineProperty([myBaselineName: 'shortname'])
            }
            actions {
                // Scrub Git repository, so file deletions will also be committed
                custom {
                    new File(git.workspace).eachFile { file ->
                        if(!file.name.startsWith(".git")) file.delete()
                    }
                }

                // Copy ClearCase view into Git repository
                copy(source.workspace, target.workspace)

                // Commit everything
                cmd 'git add .', git.workspace
                cmd 'git commit -m "$myBaselineName"', git.workspace


                def artifact = new File(git.workspace, 'artifacts/xyz.zip')
                art.publish("com/blu/xyz/${myBaselineName}/xyz-${myBaselineName}.zip", artifact)
            }
        }
    }
}
