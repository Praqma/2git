migrate('E:\\cc-to-git') {
    vob('\\2Cool_PVOB') {
        component('Model') {
            stream('server_dev') {
                migrationSteps {
                    filter {
                        criteria {
                            baselineName 'version_.*'
                        }
                        extractions {
                            baselineExtractor([name: 'shortname'])
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
                            baselineExtractor([level: 'promotionLevel'])
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