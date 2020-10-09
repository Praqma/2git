package togit.migration.sources.ccucm

import net.praqma.clearcase.PVob as CoolPVob
import net.praqma.clearcase.Rebase as CoolRebase
import net.praqma.clearcase.api.Describe
import net.praqma.clearcase.ucm.entities.Baseline as CoolBaseline
import net.praqma.clearcase.ucm.entities.Component as CoolComponent
import net.praqma.clearcase.ucm.entities.Stream as CoolStream
import net.praqma.clearcase.ucm.utils.BaselineFilter
import net.praqma.clearcase.ucm.utils.BaselineList
import net.praqma.clearcase.ucm.view.SnapshotView as CoolSnapshotView
import net.praqma.clearcase.ucm.view.SnapshotView.LoadRules2
import net.praqma.clearcase.ucm.view.UpdateView
import org.slf4j.LoggerFactory

/**
 * A Cool wrapper that adds logging.
 */
class Cool {

    final static LOG = LoggerFactory.getLogger(this.class)

    /**
     * Creates a child Stream for the given Stream at the given Baseline
     * @param parent The stream to create a child stream for
     * @param target The baseline to create the child Stream at
     * @return The new child stream
     */
    static CoolStream spawnChildStream(CoolStream parent, CoolBaseline target, String tag, boolean readOnly) {
        LOG.debug("Creating child stream of $parent.fullyQualifiedName at baseline $target.fullyQualifiedName.")
        CoolStream stream = CoolStream.create(parent, tag, readOnly, target)
        LOG.debug("Created child stream of $parent.fullyQualifiedName at baseline $target.fullyQualifiedName.")
        stream
    }

    /**
     * Creates a View for the given Stream at the given path
     * @param parent The stream to create a view for
     * @param path The path to create the view at
     * @return The new view
     */
    static CoolSnapshotView spawnView(CoolStream parent, File path, String tag) {
        LOG.debug("Creating view for $parent.fullyQualifiedName.")
        if (!path.exists()) { path.mkdirs() }
        CoolSnapshotView view = CoolSnapshotView.create(parent, path, tag)
        LOG.debug("Created view for $parent.fullyQualifiedName.")
        view
    }

    /**
     * Gets the Baselines for the given Component in the given Stream which respect the filter
     * @param target The component to get the baselines for
     * @param parent The stream to get the baselines in
     * @param filter The baseline filter
     * @return A BaselineList containing all matching baselines
     */
    static BaselineList findBaselines(CoolComponent target, CoolStream parent, BaselineFilter filter) {
        LOG.debug("Retrieving Cool baselines for $target.fullyQualifiedName in $parent.fullyQualifiedName.")
        BaselineList baselines = new BaselineList(parent, target, null).addFilter(filter).apply()
        LOG.debug("Retrieved ${baselines.size()} Cool baseline(s) for $target.fullyQualifiedName in $parent.fullyQualifiedName.")
        baselines
    }

    /**
     * Finds a Component for the given Component name
     * @param componentName The name of the component
     * @param pvob The PVob the Component is in
     * @return The component
     */
    static CoolComponent findComponent(String componentName, CoolPVob pvob) {
        LOG.debug("Retrieving Cool component $componentName.")
        CoolComponent component = CoolComponent.get(componentName, pvob)
        LOG.debug("Retrieved Cool component $component.fullyQualifiedName.")
        component
    }

    /**
     * Finds a list of component selectors representing all the modifiable loadComponents in the given stream
     * @param stream The fully qualified name of the stream to get the component selectors for
     * @return the modifiable component selectors as Strings
     */
    static List<String> findModifiableComponentSelectors(String stream) {
        new Describe(CoolStream.get(stream).project.integrationStream)
            .addModifier(new Describe.Property('mod_comps').extended(true))
            .execute().first().split(' ').toList()
    }

    /**
     * Finds a list of component selectors representing all the non-modifiable loadComponents in the given stream
     * @param stream The fully qualified name of the stream to get the component selectors for
     * @return the non-modifiable component selectors as Strings
     */
    static List<String> findNonModifiableComponentSelectors(String stream) {
        new Describe(CoolStream.get(stream).project.integrationStream)
            .addModifier(new Describe.Property('non_mod_comps').extended(true))
            .execute().first().split(' ').toList()
    }

    /**
     * Finds a PVob for the given PVob name
     * @param pvobName The name of the PVob
     * @return The PVob
     */
    static CoolPVob findPVob(String pvobName) {
        LOG.debug("Retrieving Cool vob $pvobName.")
        CoolPVob pvob = new CoolPVob(pvobName)
        pvob.load()
        LOG.debug("Retrieved Cool vob $pvob.fullyQualifiedName.")
        pvob
    }

    /**
     * Finds a Stream for the given Stream name
     * @param streamName The name of the stream
     * @param vob The PVob the stream is in
     * @return The stream
     */
    static CoolStream findStream(String streamName, CoolPVob vob) {
        LOG.debug("Retrieving Cool stream $streamName.")
        CoolStream stream = CoolStream.get(streamName, vob)
        LOG.debug("Retrieved Cool stream $stream.fullyQualifiedName")
        stream
    }

    /**
     * Rebases given Baseline onto the given View.
     * @param target The Cool Baseline to rebase.
     * @param coolView The Cool View to rebase onto.
     */
    static void rebase(CoolBaseline target, CoolSnapshotView coolView) {
        LOG.debug("Rebasing $target.fullyQualifiedName onto $coolView.fullyQualifiedName.")
        new CoolRebase(coolView).addBaseline(target).rebase(true)
        LOG.debug("Rebased $target.fullyQualifiedName onto $coolView.fullyQualifiedName.")
    }

    /**
     * Updates the given View.
     * @param coolView The Cool View to update.
     */
    static void updateView(CoolSnapshotView coolView, CoolSnapshotView.Components loadComponents) {
        LOG.debug("Updating view $coolView.fullyQualifiedName.")
        new UpdateView(coolView).swipe().setLoadRules(new LoadRules2(loadComponents)).update()
        LOG.debug("Updated view $coolView.fullyQualifiedName.")
    }
}
