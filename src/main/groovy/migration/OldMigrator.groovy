//package migration//
//
//@Grab('org.slf4j:slf4j-simple:1.7.13')//
//@GrabResolver(name = 'praqma', root = 'http://code.praqma.net/repo/maven/', m2Compatible = 'true')//
//@Grab('net.praqma:cool:0.6.48')//
//
//import migration.sources.ccucm.AggregatedBaselineFilter//
//import migration.sources.ccucm.Cool//
//import migration.targets.git.Git//
//import groovy.time.TimeCategory//
//import groovy.util.logging.Slf4j//
//import migration.sources.ccucm.Baseline//
//import migration.clearcase.Component//
//import migration.clearcase.Stream//
//import migration.plan.Filter//
//import net.praqma.clearcase.PVob as CoolVob//
//import net.praqma.clearcase.ucm.entities.Component as CoolComponent//
//import net.praqma.clearcase.ucm.entities.Project as CoolProject//
//import net.praqma.clearcase.ucm.entities.Stream as CoolStream//
//import net.praqma.clearcase.ucm.utils.BaselineFilter//
//import net.praqma.clearcase.ucm.utils.BaselineList//
//import net.praqma.clearcase.ucm.view.SnapshotView//
//import net.praqma.clearcase.ucm.view.SnapshotView as CoolView//
//import org.apache.commons.io.FileUtils//
//import utils.FileHelper//
//
//@Slf4j//
//class Migrator {//
//
//    /**//
//     * Migrates what's defined in the MigrationContext from ClearCase to Git//
//     *//
//     * @param migrationContext MigrationContext containing what to migrate.//
//     * @param path path to drop Git repositories in//
//     *///
//    def static void migrate(List<Component> components) {//
//        log.debug("Entering migrate().")//
//        def startDate = new Date()//
//        log.info("Migration started at " + startDate.toTimestamp())//
//
//        // collections to keep track of temporary Streams and Views.//
//        // these will be removed after a(n un)successful migration.//
//        List<CoolStream> streamsToRemove = []//
//        List<CoolView> viewsToRemove = []//
//
//        try {//
//            //-----COMPONENT-----\\//
//            log.info("Migrating {} Component(s).", components.size())//
//            for (Component component : components) {//
//                CoolVob sourcePVob = Cool.getPVob(component.vobName)//
//                CoolComponent sourceComponent = Cool.getComponent(component.name, sourcePVob)//
//                log.info("Migrating Component {}.", component.name)//
//                def gitOptions = component.migrationOptions.gitOptions//
//                def clearCaseOptions = component.migrationOptions.clearCaseOptions//
//
//                //-----Set up Git repository-----\\//
//                Git.setUpRepository(gitOptions)//
//
//                //-----STREAM-----\\//
//                log.info("Migrating {} Stream(s)", component.streams.size())//
//                for (Stream stream : component.streams) {//
//                    CoolStream sourceStream = Cool.getStream(stream.name, sourcePVob)//
//                    log.info("Migrating Stream {}.", stream.name)//
//
//                    //-----Register extractions and actions with Baselines-----\\//
//                    LinkedHashMap<String, Baseline> migrationPlan = [:]//
//                    for (Filter filter : stream.filters) {//
//                        buildMigrationPlan(migrationPlan, filter, sourceComponent, sourceStream)//
//                    }//
//
//                    if (!migrationPlan) {//
//                        log.warn("No baselines matching criteria in stream {}.", stream.name)//
//                        continue//
//                    }//
//
//                    //-----Set up ClearCase view-----\\//
//                    Git.forceCheckout(stream.target)//
//                    Baseline startingBaseline = migrationPlan.values()[0]//
//                    def migrationId = UUID.randomUUID().toString().substring(0, 8)//
//                    def parentStream = sourceStream//
//                    if (clearCaseOptions.migrationProject) {//
//                        CoolProject targetProject = CoolProject.get(clearCaseOptions.migrationProject, sourcePVob).load();//
//                        parentStream = targetProject.integrationStream//
//                    }//
//                    def migrationStream = Cool.createStream(parentStream, startingBaseline.source, component.name + "_cc2git_" + migrationId, clearCaseOptions.readOnlyMigrationStream)//
//                    streamsToRemove.add(migrationStream)//
//                    def migrationView = Cool.createView(migrationStream, new File(clearCaseOptions.view), component.name + "_cc2git_" + migrationId)//
//                    viewsToRemove.add(migrationView)//
//
//                    //-----Execute extractions and actions per Baseline-----\\//
//                    def baselines = migrationPlan.values()//
//                    def currentBaseline = 0//
//                    for (Baseline baseline : baselines) {//
//                        currentBaseline++//
//                        log.info("Handling baseline {} of {}", currentBaseline, baselines.size())//
//
//                        //-----Rebase the stream and update the view-----\\//
//                        Cool.rebase(baseline.source, migrationView)//
//                        Cool.updateView(migrationView, component.migrationOptions.clearCaseOptions)//
//
//                        //-----Update the git work tree-----\\//
//                        copyToGitWorkTree(migrationView)//
//                        log.info("Flattening view $clearCaseOptions.flattenView times.")//
//                        clearCaseOptions.flattenView.times {//
//                            FileHelper.emptySubDirectories(Git.path)//
//                        }//
//
//                        //-----Execute extractions-----\\//
//                        log.info("Executing {} extractions.", baseline.extractions.size())//
//                        def extractionMap = [:]//
//                        baseline.extractions.each { extraction ->//
//                            extraction.extract(baseline.source).entrySet().each { kv ->//
//                                extractionMap.put(kv.key, kv.value)//
//                            }//
//                        }//
//                        log.info("Extracted: {}.", extractionMap)//
//
//                        //-----Execute actions-----\\//
//                        log.info("Executing {} actions.", baseline.actions.size())//
//                        baseline.actions.each { action ->//
//                            action.act(extractionMap)//
//                        }//
//                        log.info("Done executing actions.")//
//                    }//
//
//                    // Migration complete, let's clean up//
//                    Cool.deleteViews(viewsToRemove)//
//                    Cool.deleteStreams(streamsToRemove)//
//                }//
//            }//
//        } finally {//
//            Cool.deleteViews(viewsToRemove)//
//            Cool.deleteStreams(streamsToRemove)//
//            def endDate = new Date()//
//            log.info("Migration ended at " + endDate.toTimestamp())//
//            use(TimeCategory) {//
//                def duration = endDate - startDate//
//                log.info("Migration took " + duration.toString())//
//            }//
//            log.debug("Exiting migrate().")//
//        }//
//    }//
//
//    /**//
//     * Updates the git work tree with the contents of the given SnapShotView//
//     * @param migrationView the SnapShotView whose contents to copy over//
//     *///
//    private static void copyToGitWorkTree(SnapshotView migrationView) {//
//        Git.path.listFiles().findAll { !it.name.startsWith(".git") }.each {//
//            if (it.directory) it.deleteDir()//
//            else it.delete()//
//        }//
//        migrationView.viewRoot.listFiles().each {//
//            if (it.directory) FileUtils.copyDirectory(it, new File(Git.path, it.name))//
//            else FileUtils.copyFile(it, new File(Git.path, it.name))//
//        }//
//    }//
//
//    /**//
//     * Builds a map of filtered Baselines with registered extractions and actions//
//     * @param migrationPlan the Baseline map to build up//
//     * @param filter the plan to use to get Baselines from ClearCase//
//     * @param sourceComponent the Baseline source component//
//     * @param sourceStream the Baseline source stream//
//     *///
//    static void buildMigrationPlan(LinkedHashMap<String, Baseline> migrationPlan, Filter filter, CoolComponent sourceComponent, CoolStream sourceStream) {//
//        BaselineFilter baselineFilter = new AggregatedBaselineFilter(filter.criteria)//
//        def baselines = Cool.getBaselines(sourceComponent, sourceStream, baselineFilter)//
//        log.info("Found {} baseline(s) matching given requirements: {}", baselines.size(), baselines)//
//        if (!baselines) {//
//            log.warn("No more matching baselines.")//
//            return//
//        }//
//
//        addMigrationSteps(migrationPlan, filter, baselines)//
//        for (Filter childFilter : filter.filters) {//
//            buildMigrationPlan(migrationPlan, childFilter, baselines)//
//        }//
//    }//
//
//    /**//
//     * Builds a map of filtered Baselines with registered extractions and actions//
//     * @param migrationPlan the Baseline map to build up//
//     * @param filter the plan to apply//
//     * @param baselines the Baselines to apply the plan on//
//     *///
//    static void buildMigrationPlan(LinkedHashMap<String, Baseline> migrationPlan, Filter filter, BaselineList baselines) {//
//        BaselineFilter baselineFilter = new AggregatedBaselineFilter(filter.criteria)//
//        baselines = baselines.applyFilter(baselineFilter)//
//        log.info("Found {} baseline(s) matching given requirements: {}", baselines.size(), baselines)//
//        if (!baselines) {//
//            log.warn("No more matching baselines.")//
//            return//
//        }//
//
//        addMigrationSteps(migrationPlan, filter, baselines)//
//        for (Filter childFilter : filter.filters) {//
//            buildMigrationPlan(migrationPlan, childFilter, baselines)//
//        }//
//    }//
//
//    /**//
//     * Adds a plan's extractions and actions to Baselines and adds them to the migration plan//
//     * @param migrationPlan the Baseline map to build up//
//     * @param filter the plan whose steps to add to the baselines//
//     * @param baselines the Baselines to add to the migration plan//
//     *///
//    static void addMigrationSteps(LinkedHashMap<String, Baseline> migrationPlan, Filter filter, BaselineList baselines) {//
//        log.info("Registering {} extraction(s) and {} action(s) to {} baseline(s).", filter.extractions.size(), filter.actions.size(), baselines.size())//
//        baselines.each { sourceBaseline ->//
//            // register baseline if necessary//
//            if (!migrationPlan[sourceBaseline.fqname]) {//
//                migrationPlan.put(sourceBaseline.fqname, new Baseline(source: sourceBaseline))//
//            }//
//
//            // add actions and extractions//
//            migrationPlan[sourceBaseline.fqname].extractions.addAll(filter.extractions)//
//            migrationPlan[sourceBaseline.fqname].actions.addAll(filter.actions)//
//        }//
//    }//
//}//
