package examples

def tempDir = "d:/2git"

source('ccucm') {
    component "_Client@\\2Cool_PVOB"
    stream "Client_int\\2Cool_PVOB"
}

target('git') {
    user 'johan'
    email 'joa@praqma.net'
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
                copy(source.workspace, tempDir)
                flattenDir(tempDir, 3)
                copy(tempDir, target.workspace)
                emptyDir(tempDir)

                cmd 'git add .', target.workspace
                cmd 'git commit -m "$myBaselineName"', target.workspace
            }
        }
    }
}