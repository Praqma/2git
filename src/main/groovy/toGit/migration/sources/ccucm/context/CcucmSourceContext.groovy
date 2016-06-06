package toGit.migration.sources.ccucm.context

import toGit.context.base.Context
import toGit.context.traits.HasSource
import toGit.migration.sources.ccucm.CcucmOptions
import toGit.migration.sources.ccucm.CcucmSource
import groovy.util.logging.Log
import net.praqma.clearcase.ucm.view.SnapshotView

@Log
class CcucmSourceContext implements Context, HasSource {

    public CcucmSourceContext() {
        source = new CcucmSource(options: new CcucmOptions())
    }

    /**
     * Sets the target stream to migrate from
     * @param streamName Fully qualified stream name
     */
    void stream(String streamName) {
        source.options.stream = streamName
        log.info("Set stream to $streamName.")
    }

    /**
     * Sets the target component to migrate
     * @param streamName Fully qualified component name
     */
    void component(String componentName) {
        source.options.component = componentName
        log.info("Set component to $componentName.")
    }

    /**
     * Sets which subcomponents to load
     * @param target String representation of target components (all, modifiable)
     */
    void loadComponents(String target) {
        if (target.equalsIgnoreCase('all')) {
            source.options.loadComponents = SnapshotView.Components.ALL
            log.info("Set loadComponents to $target.")
        } else if (target.equalsIgnoreCase('modifiable')) {
            source.options.loadComponents = SnapshotView.Components.MODIFIABLE
            log.info("Set loadComponents to $target.")
        } else
            log.warning("Invalid ClearCase component target '$target'. Expected 'all' or 'modifiable'.")
    }

    /**
     * Creates the migration stream in given project.
     * If not set, migration stream will be created as a child stream of the migration target.
     * @param projectName the project to create the migration stream in
     */
    void migrationProject(String projectName) {
        source.options.migrationProject = projectName
        log.info("Set migrationProject to $projectName.")
    }

    /**
     * Boolean setting the migration stream to readOnly or not.
     * If set to true, migration stream will be created as read-only.
     * @param readOnly the migration stream's read-only state
     */
    void readOnlyMigrationStream(boolean readOnly = true) {
        source.options.readOnlyMigrationStream = readOnly
        log.info("Set readOnlyMigrationStream to $readOnly.")
    }

    /**
     * Sets the ClearCase view path
     */
    void workspace(String path) {
        source.workspace = path
        log.info("Set workspace to $path.")
    }
}
