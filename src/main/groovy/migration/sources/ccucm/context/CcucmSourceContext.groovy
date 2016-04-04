package migration.sources.ccucm.context

import context.base.Context
import context.traits.HasSource
import groovy.util.logging.Slf4j
import migration.sources.ccucm.CcucmOptions
import migration.sources.ccucm.CcucmSource
import net.praqma.clearcase.ucm.view.SnapshotView

@Slf4j
class CcucmSourceContext implements Context, HasSource {

    public CcucmSourceContext() {
        source = new CcucmSource(options: new CcucmOptions())
    }

    void stream(String streamName) {
        source.options.stream = streamName
    }

    void component(String componentName) {
        source.options.component = componentName
    }

    void loadComponents(String target) {
        if (target.equalsIgnoreCase('all'))
            source.options.loadComponents = SnapshotView.Components.ALL
        else if (target.equalsIgnoreCase('modifiable'))
            source.options.loadComponents = SnapshotView.Components.MODIFIABLE
        else
            log.warn("Invalid ClearCase component target '$target'. Expected 'all' or 'modifiable'.")
    }

    /**
     * Creates the migration stream in given project.
     * If not set, migration stream will be created as a child stream of the migration target.
     * @param projectName the project to create the migration stream in
     */
    void migrationProject(String projectName) {
        source.options.migrationProject = projectName
    }

    /**
     * Boolean setting the migration stream to readOnly or not.
     * If set to true, migration stream will be created as read-only.
     * @param readOnly the migration stream's read-only state
     */
    void readOnlyMigrationStream(boolean readOnly = true) {
        source.options.readOnlyMigrationStream = readOnly
    }

    /**
     * Sets the ClearCase view path
     */
    void view(String path) {
        source.options.view = path
    }

    /**
     * Integer for setting the amount of times the view directory structure is flattened
     * @param count How many times the view's child directories are emptied into the root
     */
    void flattenView(int count = 1) {
        source.options.flattenView = count
    }
}
