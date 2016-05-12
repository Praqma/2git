source('ccucm') {
    component "myComp@\\vob"
    stream "myStream@\\vob"
}

target('git') {
    user 'thierry'
    email 'thi@praqma.net'
    ignore '*.tmp', '*.bk'
}

migrate {
    before {
        actions {
            println "Source: $source.workspace"
            println "Target: $target.workspace"
        }
    }
    filters {
        filter {
            criteria {
                afterDate 'dd-MM-yyy', '01-01-2015'
            }
            extractions {
                baselineProperty([myBaselineName: 'shortname'])
            }
            actions {
                copy()
                git 'add .'
                git 'commit -m "$myBaselineName"'
            }
        }
    }
    after {
        actions {
            git 'push origin master'
        }
    }
}
