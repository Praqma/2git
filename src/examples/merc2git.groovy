source('mercurial') {
    sourceRepo('/Users/angeloron/Repos/mercurial2')
    //branch('default')
}

target('git') {
    user 'thierry'
    email 'thi@praqma.net'
    //ignore '*.tmp', '*.bk'
}

migrate {
    filters {
        filter {
            criteria {
                //afterDate('yyyy-MM-dd', '2016-04-12')
            }
            actions {
//                copy()
//                git 'add .'
//                git 'commit -m "fhdsuihfsd"'
            }
        }
    }
}
