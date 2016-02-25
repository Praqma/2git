package clearcase


import groovy.util.logging.Slf4j
import migration.ClearCaseOptions
import net.praqma.clearcase.PVob as CoolPVob
import net.praqma.clearcase.Rebase as CoolRebase
import net.praqma.clearcase.api.Describe
import net.praqma.clearcase.ucm.entities.Baseline as CoolBaseline
import net.praqma.clearcase.ucm.entities.Component as CoolComponent
import net.praqma.clearcase.ucm.entities.Stream as CoolStream
import net.praqma.clearcase.ucm.utils.BaselineFilter
import net.praqma.clearcase.ucm.utils.BaselineList
import net.praqma.clearcase.ucm.view.SnapshotView as CoolSnapshotView
import net.praqma.clearcase.ucm.view.UpdateView

/**
 * A Cool wrapper that adds logging.
 */
@Slf4j
class Cool {

    /**
     * Removes given Views. Doesn't delete the View directory.
     * @param coolViews The Cool Views to remove.
     */
    static void deleteViews(List<CoolSnapshotView> coolViews) {
        log.debug("Entering deleteViews().")
        def viewIterator = coolViews.iterator()
        while (viewIterator.hasNext()) {
            CoolSnapshotView coolView = viewIterator.next()
            String viewName = coolView.fullyQualifiedName
            log.info("Removing view {}.", viewName)
            coolView.remove()
            log.info("Removed view {}.", viewName)
            viewIterator.remove()
        }
        log.debug("Exiting deleteViews()")
    }

    /**
     * Removes given Streams.
     * @param coolStreams The Cool Streams to remove.
     */
    static void deleteStreams(List<CoolStream> coolStreams){
        log.debug("Entering deleteStreams().")
        def streamIterator = coolStreams.iterator()
        while (streamIterator.hasNext()) {
            CoolStream coolStream = streamIterator.next()
            def streamName = coolStream.fullyQualifiedName
            log.info("Removing stream {}.", streamName)
            coolStream.remove()
            log.info("Removed stream {}.", streamName)
            streamIterator.remove()
        }
        log.debug("Exiting deleteStreams()")
    }

    /**
     * Creates a child Stream for the given Stream at the given Baseline
     * @param coolStream The Cool Stream to create a child Stream for.
     * @param coolBaseline The Cool Baseline to create the child Stream at.
     * @return The new child Cool Stream.
     */
    static CoolStream createStream(CoolStream coolStream, CoolBaseline coolBaseline, String tag, boolean readOnly) {
        log.debug("Entering createStream().")
        log.info("Creating child stream of {} at baseline {}", coolStream.fullyQualifiedName, coolBaseline.fullyQualifiedName)
        def migrationStream = CoolStream.create(coolStream, tag, readOnly, coolBaseline)
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
    static CoolSnapshotView createView(CoolStream coolStream, File path, String tag) {
        log.debug("Entering createView().")
        log.info("Creating view for {}.", coolStream.fullyQualifiedName)
        def coolView = CoolSnapshotView.create(coolStream, path, tag)
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
        log.info("Retrieving Cool baselines for {} in {}", coolComponent.fullyQualifiedName, coolStream.fullyQualifiedName)
        def baselines = new BaselineList(coolStream, coolComponent, null).addFilter(baselineFilter).apply()
        log.info("Retrieved {} Cool baseline(s) for {} in {}", baselines.size(), coolComponent.fullyQualifiedName, coolStream.fullyQualifiedName)
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
        log.debug("Entering getComponent().")
        log.info("Retrieving Cool component")
        def coolComponent = CoolComponent.get(componentName, coolPVob)
        log.info("Retrieved Cool component {}", coolComponent.fqname)
        log.debug("Exiting getComponent().")
        return coolComponent
    }

    /**
     * Gets a list of component selectors representing all the modifiable loadComponents in the given stream
     * @param stream The fully qualified name of the stream to get the component selectors for
     * @return the modifiable component selectors as Strings
     */
    static List<String> getModifiableComponentSelectors(String stream) {
        return new Describe(CoolStream.get(stream).project.integrationStream).addModifier(new Describe.Property("mod_comps").extended(true)).execute().first().split(" ").toList()
    }

    /**
     * Gets a list of component selectors representing all the non-modifiable loadComponents in the given stream
     * @param stream The fully qualified name of the stream to get the component selectors for
     * @return the non-modifiable component selectors as Strings
     */
    static List<String> getNonModifiableComponentSelectors(String stream) {
        return new Describe(CoolStream.get(stream).project.integrationStream).addModifier(new Describe.Property("non_mod_comps").extended(true)).execute().first().split(" ").toList()
    }

    /**
     * Gives a Cool PVob for the given PVob name.
     * @param pvobName The name of the PVob
     * @return The Cool PVob.
     */
    static CoolPVob getPVob(String pvobName) {
        log.debug("Entering getPVob().")
        log.info("Retrieving Cool vob")
        def coolPVob = CoolPVob.get(pvobName)
        log.info("Retrieved Cool vob {}", coolPVob.fullyQualifiedName)
        log.debug("Exiting getPVob().")
        return coolPVob
    }

    /**
     * Gives a Cool Stream for the given Stream name.
     * @param streamName The name of the Stream.
     * @param vob The Cool PVob the Stream is in.
     * @return The Cool Stream.
     */
    static CoolStream getStream(String streamName, CoolPVob vob) {
        log.debug("Entering getStream().")
        log.info("Retrieving Cool stream {}", streamName)
        def coolStream = CoolStream.get(streamName, vob)
        log.info("Retrieved Cool stream {}", coolStream.fqname)
        log.debug("Exiting getStream().")
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
        new CoolRebase(coolView).addBaseline(coolBaseline).rebase(true)
        log.info("Rebased {} onto {}.", coolBaseline.fullyQualifiedName, coolView.fullyQualifiedName)
        log.debug("Exiting rebase().")
    }

    /**
     * Updates the given View.
     * @param coolView The Cool View to update.
     */
    static void updateView(CoolSnapshotView coolView, ClearCaseOptions clearCaseOptions) {
        log.debug("Entering updateView().")
        log.info("Updating view {}", coolView.fullyQualifiedName)
        def loadRules = new CoolSnapshotView.LoadRules2(clearCaseOptions.loadComponents);
        new UpdateView(coolView).setLoadRules(loadRules).update()
        log.info("Updated view {}", coolView.fullyQualifiedName)
        log.debug("Exiting updateView().")
    }
}
