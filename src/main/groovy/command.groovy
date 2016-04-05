source('ccucm') {
    component "myComp@\\vob"
    stream "bloop@\\sisi"
}

target('git') {
    user 'thierry'
    email 'thi@praqma.net'
    ignore '*.tmp', '*.bk'
}

migrate {
    before {
        actions {
            println source.dir
            println target.dir
            git 'pull origin master'
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
