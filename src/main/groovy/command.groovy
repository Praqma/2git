migrate('E:\\cc-to-git') {
    vob('\\2Cool_PVOB') {
        component('Model') {
            migrationOptions {
                git {
                    // path    'E:\\cc-to-git' //TODO?
                    ignore '.jnk'
                    user 'praqma'
                    email 'support@praqma.net'
                }
            }
            stream('server_dev2') {
                migrationSteps {
                    filter {
                        criteria {
                            baselineName 'v\\d{3}.*'
                        }
                        extractions {
                            baselineProperty([name: 'shortname'])
                        }
                        actions {
                            git 'add -A'
                            git 'commit -m\"$name\"'
                        }
                    }
                    filter {
                        criteria {
                            promotionLevels 'INITIAL'
                        }
                        extractions {
                            baselineProperty([level: 'promotionLevel'])
                        }
                        actions {
                            git 'tag \"$level-$name\"'
                        }
                    }

                }
            }
        }
    }
}