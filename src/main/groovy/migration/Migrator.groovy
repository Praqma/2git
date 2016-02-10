package migration

@Grab('org.slf4j:slf4j-simple:1.7.13')
@GrabResolver(name = 'praqma', root = 'http://code.praqma.net/repo/maven/', m2Compatible = 'true')
@Grab('net.praqma:cool:0.6.48')

import clearcase.AggregatedBaselineFilter
import clearcase.Cool
import git.Git
import groovy.util.logging.Slf4j
import migration.clearcase.Baseline
import migration.clearcase.Component
import migration.clearcase.Stream
import migration.clearcase.Vob
import migration.filter.Filter
import net.praqma.clearcase.PVob as CoolVob
import net.praqma.clearcase.ucm.entities.Component as CoolComponent
import net.praqma.clearcase.ucm.entities.Stream as CoolStream
import net.praqma.clearcase.ucm.utils.BaselineFilter
import net.praqma.clearcase.ucm.utils.BaselineList
import net.praqma.clearcase.ucm.view.SnapshotView as CoolView
import org.apache.commons.io.FileUtils

@Slf4j
class Migrator {
    /**
     * Migrates what's defined in the MigrationContext from ClearCase to Git
     *
     * @param migrationContext MigrationContext containing what to migrate.
     * @param path path to drop Git repositories in
     */
    def static void migrate(List<Vob> vobs) {
        log.debug("Entering migrate().")
        def startDate = new Date()
        log.info("Migration started at " + startDate.toTimestamp())

        // collections to keep track of temporary Streams and Views.
        // these will be removed after a(n un)successful migration.
        List<CoolStream> streamsToRemove = []
        List<CoolView> viewsToRemove = []

        try {
            //-----VOB-----\\
            log.info("Migrating from {} Vob(s).", vobs.size())
            for (Vob vob : vobs) {
                CoolVob sourcePVob = Cool.getPVob(vob.name)
                log.info("Migrating from Vob {}.", vob.name)

                //-----COMPONENT-----\\
                log.info("Migrating {} Component(s).", vob.components.size())
                for (Component component : vob.components) {
                    CoolComponent sourceComponent = Cool.getComponent(component.name, sourcePVob)
                    log.info("Migrating Component {}.", component.name)


                    //-----set up Git repository-----\\
                    def gitOptions = component.migrationOptions.gitOptions
                    File gitDir = new File(gitOptions.dir)
                    File workTree = new File(gitOptions.workTree)
                    Git.path = workTree
                    if (!workTree.exists()) FileUtils.forceMkdir(workTree)
                    if (!gitDir.exists()) {
                        log.info("Git dir {} does not exist, performing first time setup.", gitDir)
                        FileUtils.forceMkdir(gitDir)
                        Git.callOrDie("init --separate-git-dir $gitOptions.dir")
                    }
                    Git.configureRepository(gitOptions)

                    //-----STREAM-----\\
                    log.info("Migrating {} Stream(s)", component.streams.size())
                    for (Stream stream : component.streams) {
                        CoolStream sourceStream = Cool.getStream(stream.name, sourcePVob)
                        log.info("Migrating Stream {}.", stream.name)

                        //-----register extractions and actions with Baselines-----\\
                        LinkedHashMap<String, Baseline> migrationPlan = [:]
                        for (Filter filter : stream.filters) {
                            buildMigrationPlan(migrationPlan, filter, sourceComponent, sourceStream)
                        }

                        if (!migrationPlan) {
                            log.warn("No baselines matching criteria in stream {}.", stream.name)
                            continue
                        }

                        //-----Set up Git work tree-----\\
                        Git.forceCheckout(stream.target)
                        Baseline startingBaseline = migrationPlan.values()[0]
                        def id = UUID.randomUUID().toString().substring(0,8)
                        def migrationStream = Cool.createStream(sourceStream, startingBaseline.source, component.name + "_cc2git_" + id)
                        streamsToRemove.add(migrationStream)
                        def migrationView = Cool.createView(migrationStream, workTree, component.name + "_cc2git_" + id)
                        viewsToRemove.add(migrationView)


                        //-----execute extractions and actions per Baseline-----\\
                        def baselines = migrationPlan.values()
                        def baselineCount = baselines.size()
                        def currentBaseline = 0
                        for (Baseline baseline : baselines){
                            currentBaseline++
                            log.info("Handling baseline {} of {}", currentBaseline, baselineCount)

                            //-----rebase the stream and update the view-----\\
                            Cool.rebase(baseline.source, migrationView)
                            Cool.updateView(migrationView, component.migrationOptions.clearCaseOptions)
                            // the .git and .gitignore file get removed during the update, add them again
                            new File(workTree, '.git').write("gitdir: $gitOptions.dir")
                            Git.writeGitIgnore(gitOptions)

                            log.info("Executing {} extractions.", baseline.extractions.size())
                            def extractionMap = [:]
                            baseline.extractions.each { extraction ->
                                extraction.extract(baseline.source).entrySet().each { kv ->
                                    extractionMap.put(kv.key, kv.value)
                                }
                            }
                            log.info("Extracted: {}.", extractionMap)

                            log.info("Executing {} actions.", baseline.actions.size())
                            baseline.actions.each { action ->
                                action.act(extractionMap)
                            }
                            log.info("Done executing actions.")
                        }

                        // migration complete, let's clean up
                        Cool.deleteViews(viewsToRemove)
                        Cool.deleteStreams(streamsToRemove)
                    }
                }
            }
        } finally {
            Cool.deleteViews(viewsToRemove)
            Cool.deleteStreams(streamsToRemove)
            def endDate = new Date()
            log.info("Migration ended at " + endDate.toTimestamp())
            use(groovy.time.TimeCategory) {
                def duration = endDate - startDate
                log.info("Migration took " + duration.toString())
            }
            log.debug("Exiting migrate().")
        }
    }

    /**
     * Builds a map of filtered Baselines with registered extractions and actions
     * @param migrationPlan the Baseline map to build up
     * @param filter the filter to use to get Baselines from ClearCase
     * @param sourceComponent the Baseline source component
     * @param sourceStream the Baseline source stream
     */
    static void buildMigrationPlan(LinkedHashMap<String, Baseline> migrationPlan, Filter filter, CoolComponent sourceComponent, CoolStream sourceStream) {
        BaselineFilter baselineFilter = new AggregatedBaselineFilter(filter.criteria)
        def baselines = Cool.getBaselines(sourceComponent, sourceStream, baselineFilter)
        log.info("Found {} baseline(s) matching given requirements: {}", baselines.size(), baselines)
        if (!baselines) {
            log.warn("No more matching baselines.")
            return
        }

        addMigrationSteps(migrationPlan, filter, baselines)
        for(Filter childFilter : filter.filters){
            buildMigrationPlan(migrationPlan, childFilter, baselines)
        }
    }

    /**
     * Builds a map of filtered Baselines with registered extractions and actions
     * @param migrationPlan the Baseline map to build up
     * @param filter the filter to apply
     * @param baselines the Baselines to apply the filter on
     */
    static void buildMigrationPlan(LinkedHashMap<String, Baseline> migrationPlan, Filter filter, BaselineList baselines) {
        BaselineFilter baselineFilter = new AggregatedBaselineFilter(filter.criteria)
        baselines = baselines.applyFilter(baselineFilter)
        log.info("Found {} baseline(s) matching given requirements: {}", baselines.size(), baselines)
        if (!baselines) {
            log.warn("No more matching baselines.")
            return
        }

        addMigrationSteps(migrationPlan, filter, baselines)
        for(Filter childFilter : filter.filters){
            buildMigrationPlan(migrationPlan, childFilter, baselines)
        }
    }

    /**
     * Adds a filter's extractions and actions to Baselines and adds them to the migration plan
     * @param migrationPlan the Baseline map to build up
     * @param filter the filter whose steps to add to the baselines
     * @param baselines the Baselines to add to the migration plan
     */
    static void addMigrationSteps(LinkedHashMap<String, Baseline> migrationPlan, Filter filter, BaselineList baselines) {
        log.info("Registering {} extraction(s) and {} action(s) to {} baseline(s).", filter.extractions.size(), filter.actions.size(), baselines.size())
        baselines.each { sourceBaseline ->
            // register baseline if necessary
            if (!migrationPlan[sourceBaseline.fqname]){
                migrationPlan.put(sourceBaseline.fqname, new Baseline(source: sourceBaseline))
            }

            // add actions and extractions
            migrationPlan[sourceBaseline.fqname].extractions.addAll(filter.extractions)
            migrationPlan[sourceBaseline.fqname].actions.addAll(filter.actions)
        }
    }
}
