from('ccucm') {
    component "myComp@\\vob"
    stream "bloop@\\sisi"
}

to('git') {
    user 'thierry'
    email 'thi@praqma.net'
    ignore '*.tmp', '*.bk'
}

migrate {
    before {
        actions {
            git 'pull'
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
            git 'push'
        }
    }
}
