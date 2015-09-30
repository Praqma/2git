package clearCase

@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
import net.praqma.clearcase.PVob as CoolPVob
import net.praqma.clearcase.Rebase as CoolRebase
import net.praqma.clearcase.ucm.entities.Baseline as CoolBaseline
import net.praqma.clearcase.ucm.entities.Component as CoolComponent
import net.praqma.clearcase.ucm.entities.Stream as CoolStream
import net.praqma.clearcase.ucm.utils.BaselineFilter
import net.praqma.clearcase.ucm.utils.BaselineList
import net.praqma.clearcase.ucm.view.SnapshotView as CoolSnapshotView
import net.praqma.clearcase.ucm.view.UpdateView

@Slf4j
/**
 * A helpful wrapper for Cool that adds logging.
 */
class Cool {

    /**
     * Removes given Views and Streams.
     * @param coolViews The Cool Views to remove.
     * @param coolStreams The Cool Streams to remove.
     */
    static void cleanUp(List<CoolSnapshotView> coolViews, List<CoolStream> coolStreams) {
        log.debug("Entering cleanup().")
        def viewIterator = coolViews.iterator()
        while (viewIterator.hasNext()) {
            CoolSnapshotView coolView = viewIterator.next()
            log.info("Removing view {}.", coolView.fullyQualifiedName)
            coolView.remove()
            viewIterator.remove()
        }
        def streamIterator = coolStreams.iterator()
        while (streamIterator.hasNext()) {
            CoolStream coolStream = streamIterator.next()
            log.info("Removing stream {}.", coolStream.fullyQualifiedName)
            coolStream.remove()
            streamIterator.remove()
        }
        log.debug("Exiting cleanup()")
    }

    /**
     * Creates a child Stream for the given Stream at the given Baseline
     * @param coolStream The Cool Stream to create a child Stream for.
     * @param coolBaseline The Cool Baseline to create the child Stream at.
     * @return The new child Cool Stream.
     */
    static CoolStream createStream(CoolStream coolStream, CoolBaseline coolBaseline, String tag) {
        log.debug("Entering createStream().")
        log.info("Creating child stream of {} at baseline {}", coolStream.fullyQualifiedName, coolBaseline.fullyQualifiedName)
        def migrationStream = CoolStream.create(coolStream, tag, false, coolBaseline)
        log.info("Created child stream of {} at baseline {}", coolStream.fullyQualifiedName, coolBaseline.fullyQualifiedName)
        log.debug("Exiting createStream().")
        return migrationStream
    }

    /**
     * Creates a View for the given Stream at the given path.
     * @param coolStream The Cool Stream to create a Cool View for.
     * @param path The path to create the Cool View at.
     * @return The new Cool View.
     */
    static CoolSnapshotView createView(CoolStream coolStream, String path, String tag) {
        log.debug("Entering createView().")
        log.info("Creating view for {}.", coolStream.fullyQualifiedName)
        def coolView = CoolSnapshotView.create(coolStream, new File(path), tag)
        log.info("Created view for {}.", coolStream.fullyQualifiedName)
        log.debug("Exiting createView().")
        return coolView
    }

    /**
     * Gets the Baselines for the given Component in the given Stream which respect the given name Regex and Promotion Levels.
     * @param coolComponent The Cool Component to get the baselines for.
     * @param coolStream The Cool Stream to get the Baselines in.
     * @param baselineFilter The filter for the Baselines.
     * @return A BaselineList containing all matching Baselines.
     */
    static BaselineList getBaselines(CoolComponent coolComponent, CoolStream coolStream, BaselineFilter baselineFilter) {
        log.debug("Entering getBaselines().")
        log.info("Getting baselines for {} in {}", coolComponent.fullyQualifiedName, coolStream.fullyQualifiedName)
        def baselines = new BaselineList(coolStream, coolComponent, null).addFilter(baselineFilter).apply()
        log.info("Found {} baselines matching given requirements: {}", baselines.size(), baselines)
        log.debug("Exiting getBaselines().")
        return baselines
    }

    /**
     * Gives a Cool Component for the given Component name.
     * @param componentName The name of the Component.
     * @param coolPVob The Cool PVob the Component is in
     * @return The Cool Component.
     */
    static CoolComponent getComponent(String componentName, CoolPVob coolPVob) {
        println "getting component"
        def coolComponent = CoolComponent.get(componentName, coolPVob)
        println "got component " + coolComponent.fqname
        return coolComponent
    }

    /**
     * Gives a Cool PVob for the given PVob name.
     * @param pvobName The name of the PVob
     * @return The Cool PVob.
     */
    static CoolPVob getPVob(String pvobName) {
        println "getting vob"
        def coolPVob = CoolPVob.get(pvobName)
        println "got vob " + coolPVob.fullyQualifiedName
        return coolPVob
    }

    /**
     * Gives a Cool Stream for the given Stream name.
     * @param streamName The name of the Stream.
     * @param vob The Cool PVob the Stream is in.
     * @return The Cool Stream.
     */
    static CoolStream getStream(String streamName, CoolPVob vob) {
        println "getting stream " + streamName
        def coolStream = CoolStream.get(streamName, vob)
        println "got stream " + coolStream.fqname
        return coolStream
    }

    /**
     * Rebases given Baseline onto the given View.
     * @param coolBaseline The Cool Baseline to rebase.
     * @param coolView The Cool View to rebase onto.
     */
    static void rebase(CoolBaseline coolBaseline, CoolSnapshotView coolView) {
        log.debug("Entering rebase().")
        log.info("Rebasing {} onto {}.", coolBaseline.fullyQualifiedName, coolView.fullyQualifiedName)
        def coolRebase = new CoolRebase(coolView).addBaseline(coolBaseline).rebase(true)
        log.info("Rebased {} onto {}.", coolBaseline.fullyQualifiedName, coolView.fullyQualifiedName)
        log.debug("Exiting rebase().")
    }

    /**
     * Updates the given View.
     * @param coolView The Cool View to update.
     */
    static void updateView(CoolSnapshotView coolView) {
        log.debug("Entering updateView().")
        log.info("Updating {}", coolView.fullyQualifiedName)
        def loadRules = new CoolSnapshotView.LoadRules2(CoolSnapshotView.Components.MODIFIABLE);
        def updateView = new UpdateView(coolView).setLoadRules(loadRules).update()
        log.debug("Exiting updateView().")
    }

}
