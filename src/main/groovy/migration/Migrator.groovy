package migration

@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
@GrabResolver(name = 'praqma', root = 'http://code.praqma.net/repo/maven/', m2Compatible = 'true')
@Grab('net.praqma:cool:0.6.45')
import clearcase.Cool
import git.Git
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

        // Collections to keep track of temporary Streams and Views.
        // These will be removed after a(n un)successful migration.
        List<CoolStream> streamsToRemove = []
        List<CoolView> viewsToRemove = []

        try {
            //-----VOB-----\\
            log.info("Migrating from {} Vob(s).", vobs.size())
            for(Vob vob : vobs) {
                CoolVob sourcePVob = Cool.getPVob(vob.name)
                log.info("Migrating from Vob {}.", vob.name)

                //-----COMPONENT-----\\
                log.info("Migrating {} Component(s).", vob.components.size())
                for(Component component : vob.components) {
                    CoolComponent sourceComponent = Cool.getComponent(component.name, sourcePVob)
                    log.info("Migrating Component {}.", component.name)

                    File repository = component.setUpRepository()

                    //-----STREAM-----\\
                    log.info("Migrating {} Stream(s)", component.streams.size())
                    for(Stream stream : component.streams) {
                        CoolStream sourceStream = Cool.getStream(stream.name, sourcePVob)
                        log.info("Migrating Stream {}.", stream.name)

                        BaselineList baselines = null
                        LinkedHashMap<String, Baseline> baselineMap = [:]
                        for(Filter filter : stream.filters) {
                            //-----APPLY CRITERIA-----\\
                            baselines = filter.getBaselines(baselines, sourceComponent, sourceStream)
                            if(!baselines){
                                log.warn("No further baseline matches in step {}.", stream.filters.indexOf(filter) + 1)
                                break
                            }

                            baselines.each { sourceBaseline ->
                                if(!baselineMap[sourceBaseline.fqname])
                                    baselineMap.put(sourceBaseline.fqname, new Baseline(source: sourceBaseline))
                            }

                            //-----REGISTER EXTRACTIONS AND ACTIONS-----\\
                            baselines.each { sourceBaseline ->
                                baselineMap[sourceBaseline.fqname].extractions.addAll(filter.extractions)
                                baselineMap[sourceBaseline.fqname].actions.addAll(filter.actions)
                            }
                        }

                        if (!baselineMap) {
                            log.warn("No baselines to migrate in {}.", stream.name)
                            continue
                        }

                        Git.forceCheckout(stream.target)

                        def firstBaseline = baselineMap.values()[0]
                        def migrationStream = Cool.createStream(sourceStream, firstBaseline.source, component.name + "_cc-to-git")
                        streamsToRemove.add(migrationStream)
                        // Create a temporary View inside the Git repository.
                        // Because ClearCase can't make Views in existing folders, Cool deletes the branch path.
                        // We temporarily move the .git dir to avoid it being deleted by Cool.
                        FileUtils.moveDirectory(new File(repository.path, ".git"), new File(repository.parentFile.path, ".git"))
                        FileUtils.moveFile(new File(repository.path, ".gitignore"), new File(repository.parentFile.path, ".gitignore"))
                        def migrationView = Cool.createView(migrationStream, repository.path, component.name + "_cc-to-git")
                        viewsToRemove.add(migrationView)
                        FileUtils.moveDirectory(new File(repository.parentFile.path, ".git"), new File(repository.path, ".git"))
                        FileUtils.moveFile(new File(repository.parentFile.path, ".gitignore"), new File(repository.path, ".gitignore"))

                        def currentBaseline = 0
                        def baselineCount = baselineMap.values().size()
                        baselineMap.values().each { baseline ->
                            currentBaseline++
                            log.info("Handling baseline {} of {}", currentBaseline, baselineCount)

                            Cool.rebase(baseline.source, migrationView)
                            Cool.updateView(migrationView)

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
                        Cool.cleanUp(viewsToRemove, streamsToRemove)
                    }
                }
            }
        } finally {
            Cool.cleanUp(viewsToRemove, streamsToRemove)
            log.debug("Exiting migrate().")
        }
    }
}
