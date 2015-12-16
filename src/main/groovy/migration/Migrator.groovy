package migration

@GrabResolver(name = 'praqma', root = 'http://code.praqma.net/repo/maven/', m2Compatible = 'true')
@Grab('net.praqma:cool:0.6.45')
import clearcase.Cool
import git.Git
@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
import migration.clearcase.Baseline
import migration.clearcase.Component
import migration.clearcase.Stream
import migration.clearcase.Vob
import migration.filter.Filter
import net.praqma.clearcase.PVob as CoolVob
import net.praqma.clearcase.ucm.entities.Component as CoolComponent
import net.praqma.clearcase.ucm.entities.Stream as CoolStream
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

        // Collections to keep track of temporary Streams and Views.
        // These will be removed after a(n un)successful migration.
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

                    def gitOptions = component.migrationOptions.gitOptions
                    File gitDir = new File(gitOptions.dir)
                    File workTree = new File(gitOptions.workTree)
                    Git.path = workTree
                    if(!workTree.exists()) FileUtils.forceMkdir(workTree)
                    if(!gitDir.exists()){
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

                        BaselineList baselines = null
                        LinkedHashMap<String, Baseline> baselineMap = [:]
                        for (Filter filter : stream.filters) {
                            //-----APPLY CRITERIA-----\\
                            baselines = filter.getBaselines(baselines, sourceComponent, sourceStream)
                            if (!baselines) {
                                log.warn("No further baseline matches in step {}.", stream.filters.indexOf(filter) + 1)
                                break
                            }

                            //-----REGISTER EXTRACTIONS AND ACTIONS-----\\
                            baselines.each { sourceBaseline ->
                                if (!baselineMap[sourceBaseline.fqname])
                                    baselineMap.put(sourceBaseline.fqname, new Baseline(source: sourceBaseline))

                                baselineMap[sourceBaseline.fqname].extractions.addAll(filter.extractions)
                                baselineMap[sourceBaseline.fqname].actions.addAll(filter.actions)
                            }
                        }

                        if (!baselineMap) {
                            log.warn("No baselines to migrate in {}.", stream.name)
                            continue
                        }

                        Git.forceCheckout(stream.target)

                        Baseline startingBaseline = baselineMap.values()[0]
                        def migrationStream = Cool.createStream(sourceStream, startingBaseline.source, component.name + "_cc-to-git")
                        streamsToRemove.add(migrationStream)

                        // Create a temporary View inside the Git work tree.
                        def migrationView = Cool.createView(migrationStream, workTree, component.name + "_cc-to-git")
                        viewsToRemove.add(migrationView)

                        def currentBaseline = 0
                        def baselineCount = baselineMap.values().size()
                        baselineMap.values().each { baseline ->
                            currentBaseline++
                            log.info("Handling baseline {} of {}", currentBaseline, baselineCount)

                            Cool.rebase(baseline.source, migrationView)
                            Cool.updateView(migrationView)
                            new File(workTree, '.git').write("gitdir: $gitOptions.dir")
                            Git.writeGitIgnore(gitOptions)

                            //-----EXTRACTIONS-----\\
                            log.info("Extracting...")
                            def extractionMap = [:]
                            baseline.extractions.each { extraction ->
                                extraction.extract(baseline.source).entrySet().each { kv ->
                                    extractionMap.put(kv.key, kv.value)
                                }
                            }
                            log.info("Extracted: {}.", extractionMap)

                            //-----ACTIONS-----\\
                            baseline.actions.each { action ->
                                action.act(extractionMap)
                            }
                        }

                        //Migration complete, let's clean up
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
}
