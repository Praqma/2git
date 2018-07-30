package togit.migration.sources.ccucm.context

import net.praqma.clearcase.ucm.view.SnapshotView
import org.slf4j.LoggerFactory
import togit.context.SourceContext
import togit.migration.sources.ccucm.CcucmOptions
import togit.migration.sources.ccucm.CcucmSource

class CcucmSourceContext extends SourceContext {

    final static LOG = LoggerFactory.getLogger(this.class)

    CcucmSourceContext() {
        source = new CcucmSource(options:new CcucmOptions())
    }

    /**
     * Sets the target stream to migrate from
     * @param streamName Fully qualified stream name
     */
    void stream(String streamName) {
        source.options.stream = streamName
        LOG.debug("Set stream to $streamName.")
    }

    /**
     * Sets the target component to migrate
     * @param streamName Fully qualified component name
     */
    void component(String componentName) {
        source.options.component = componentName
        LOG.debug("Set component to $componentName.")
    }

    /**
     * Sets which subcomponents to load
     * @param target String representation of target components (all, modifiable)
     */
    void loadComponents(String target) {
        if (target.equalsIgnoreCase('all')) {
            source.options.loadComponents = SnapshotView.Components.ALL
            LOG.debug("Set loadComponents to $target.")
        } else if (target.equalsIgnoreCase('modifiable')) {
            source.options.loadComponents = SnapshotView.Components.MODIFIABLE
            LOG.debug("Set loadComponents to $target.")
        } else {
            String message = "Invalid ClearCase component target '$target'. Expected 'all' or 'modifiable'."
            LOG.error(message)
            throw new IllegalArgumentException(message)
        }
    }

    /**
     * Creates the migration stream in given project.
     * If not set, migration stream will be created as a child stream of the migration target.
     * @param projectName the project to create the migration stream in
     */
    void migrationProject(String projectName) {
        source.options.migrationProject = projectName
        LOG.debug("Set migrationProject to $projectName.")
    }

    /**
     * Boolean setting the migration stream to readOnly or not.
     * If set to true, migration stream will be created as read-only.
     * @param readOnly the migration stream's read-only state
     */
    void readOnlyMigrationStream(boolean readOnly = true) {
        source.options.readOnlyMigrationStream = readOnly
        LOG.debug("Set readOnlyMigrationStream to $readOnly.")
    }

    /**
     * Sets the ClearCase view path
     */
    void workspace(String path) {
        source.workspace = path
        LOG.debug("Set workspace to $path.")
    }
}
