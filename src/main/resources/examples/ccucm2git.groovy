package examples

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