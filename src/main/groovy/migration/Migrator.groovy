package migration

@GrabResolver(name = 'praqma', root = 'http://code.praqma.net/repo/maven/', m2Compatible = 'true')
@Grab('net.praqma:cool:0.6.45')

import clearCase.AggregatedBaselineFilter
import clearCase.Cool
import git.Git
@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
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
    def static void migrate(MigrationContext migrationContext, String path) {
        log.debug("Entering migrate().")

        // Collections to keep track of temporary Streams and Views.
        // These will be removed after a(n un)successful migration.
        List<CoolStream> streamsToRemove = []
        List<CoolView> viewsToRemove = []

        try {
            //-----VOB-----\\
            log.info("Migrating from {} Vob(s).", migrationContext.vobs.size())
            for(Vob vob : migrationContext.vobs) {
                CoolVob sourcePVob = Cool.getPVob(vob.name)
                log.info("Migrating from Vob {}.", vob.name)

                //-----COMPONENT-----\\
                log.info("Migrating {} Component(s).", vob.components.size())
                for(Component component : vob.components) {
                    CoolComponent sourceComponent = Cool.getComponent(component.name, sourcePVob)
                    log.info("Migrating Component {}.", component.name)

                    File repository = new File(path + "/" + component.name)
                    Git.path = repository.path
                    if(!repository.exists()) {
                        log.info("Path {} does not exist, performing first time setup.", path)
                        FileUtils.forceMkdir(new File(Git.path))
                        Git.callOrDie('init')
                    }
                    Git.configureRepository(component.migrationOptions.gitOptions)

                    //-----STREAM-----\\
                    log.info("Migrating {} Stream(s)", component.streams.size())
                    for(Stream stream : component.streams) {
                        CoolStream sourceStream = Cool.getStream(stream.name, sourcePVob)
                        log.info("Migrating Stream {}.", stream.name)

                        BaselineList baselines = null
                        LinkedHashMap<String, Baseline> baselineMap = [:]
                        for(Filter filter : stream.filters) {
                            //-----CRITERIA-----\\
                            BaselineFilter baselineFilter = new AggregatedBaselineFilter(filter.criteria)
                            baselines = baselines ? baselines.applyFilter(baselineFilter) : Cool.getBaselines(sourceComponent, sourceStream, baselineFilter)
                            log.info("Found {} baselines matching given requirements: {}", baselines.size(), baselines)
                            if(!baselines){
                                log.warn("No further baseline matches in step {}.", stream.filters.indexOf(filter) + 1)
                                break
                            }
                            baselines.each { sourceBaseline ->
                                if(!baselineMap.get(sourceBaseline.fqname))
                                    baselineMap.put(sourceBaseline.fqname, new Baseline(source: sourceBaseline))
                            }

                            //-----REGISTER EXTRACTIONS AND ACTIONS-----\\
                            baselines.each { sourceBaseline ->
                                baselineMap.get(sourceBaseline.fqname).extractions.addAll(filter.extractions)
                                baselineMap[sourceBaseline.fqname].actions.addAll(filter.actions)
                            }
                        }

                        if (!baselineMap) {
                            log.warn("No baselines to migrate in {}.", stream.name)
                            continue
                        }

                        if(!Git.call("rev-parse --verify " + stream.name))
                            Git.callOrDie("checkout " + stream.name)
                        else
                            Git.callOrDie("checkout -b " + stream.name)

                        def firstBaseline = baselineMap.values()[0]
                        def migrationStream = Cool.createStream(sourceStream, firstBaseline.source, component.name + "_cc-to-git")
                        streamsToRemove.add(migrationStream)
                        // Create a temporary View inside the Git repository.
                        // Because ClearCase can't make Views in existing folders, Cool deletes the target path.
                        // We temporarily move the .git dir to avoid it being deleted by Cool.
                        FileUtils.moveDirectory(new File(Git.path, ".git"), new File(path, ".git"))
                        FileUtils.moveFile(new File(Git.path, ".gitignore"), new File(path, ".gitignore"))
                        def migrationView = Cool.createView(migrationStream, Git.path, component.name + "_cc-to-git")
                        viewsToRemove.add(migrationView)
                        FileUtils.moveDirectory(new File(path, ".git"), new File(Git.path, ".git"))
                        FileUtils.moveFile(new File(path, ".gitignore"), new File(Git.path, ".gitignore"))

                        baselineMap.values().each { baseline ->
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
