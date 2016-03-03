def componentName = '_Client@\\2Cool_PVOB'
def streamName = 'Client_migr@\\2Cool_PVOB'
def startDate = '31-05-2015'

def gitDir = "e:/cc2git/$componentName/.git"
def gitWorkTree = "e:/cc2git/$componentName/view"

migrate {
    component(componentName) {
        migrationOptions {
            git {
                dir gitDir
                workTree gitWorkTree
                ignore 'build.log', 'test.log'
                user 'praqma'
                email 'support@praqma.net'
            }
            clearCase {
                loadComponents 'all'
                migrationProject 'Jenkins'
                readOnlyMigrationStream true
            }
        }
        stream(streamName) {
            branch 'master'
            migrationSteps {
                filter {
                    criteria {
                        afterDate 'dd-MM-yyy', startDate
                        promotionLevels 'INITIAL'
                    }
                    extractions {
                        baselineProperty([name: 'shortname', fqname: 'fqname'])
                    }
                    actions {
                        git 'add .'
                        git 'commit -m"$name"'
                        git 'notes add -m"$fqname" HEAD'
                    }
                    filter {
                        criteria {
                            promotionLevels 'INITIAL'
                        }
                        extractions {
                            baselineProperty([level: 'promotionLevel'])
                        }
                        actions {
                            git 'tag $level-$name'
                        }
                    }
                }
            }
        }
    }
}