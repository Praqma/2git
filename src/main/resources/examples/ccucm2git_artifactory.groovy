package examples

def tempDir = "d:/2git"

source('ccucm') {
    component "_Client@\\2Cool_PVOB"
    stream "Client_int\\2Cool_PVOB"
}

target('git', 'git') {
    user 'johan'
    email 'joa@praqma.net'
}

target('artifactory', 'art') {
    host 'artifactory.blumanufacturing.com'
    port 8081
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
                cmd 'git add .', git.workspace
                cmd 'git commit -m "$myBaselineName"', git.workspace
                def artifact = new File(git.workspace, 'build/client.zip')
                publish("/artifactory/libs-snapshot-local/com/blu/foober/${version}/client-${version}.zip", artifact)
            }
        }
    }
}