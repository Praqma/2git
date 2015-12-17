def vobName = '\\2Cool_PVOB'
def componentName = '_Client'
def streamName = 'Client_migr'
def startDate = '31-05-2015'

def gitDir = "e:/cc-to-git/$componentName/.git"
def gitWorkTree = "e:/cc-to-git/$componentName/view"

migrate{
    vob(vobName) {
        component(componentName) {
            migrationOptions {
                git {
                    dir	gitDir
                    workTree gitWorkTree
                    ignore 'build.log', 'test.log'
                    user 'praqma'
                    email 'support@praqma.net'
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
}