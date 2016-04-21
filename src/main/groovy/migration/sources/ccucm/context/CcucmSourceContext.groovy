package migration.sources.ccucm.context

import context.base.Context
import context.traits.HasSource
import groovy.util.logging.Slf4j
import migration.sources.ccucm.CcucmOptions
import migration.sources.ccucm.CcucmSource
import net.praqma.clearcase.ucm.view.SnapshotView
import org.codehaus.groovy.tools.StringHelper

@Slf4j
class CcucmSourceContext implements Context, HasSource {

    public CcucmSourceContext() {
        source = new CcucmSource(options: new CcucmOptions())
    }

    void stream(String streamName) {
        source.options.stream = streamName
        log.info("Set stream to $streamName.")
    }

    void component(String componentName) {
        source.options.component = componentName
        log.info("Set component to $componentName.")
    }

    void loadComponents(String target) {
        if (target.equalsIgnoreCase('all')) {
            source.options.loadComponents = SnapshotView.Components.ALL
            log.info("Set loadComponents to $target.")
        } else if (target.equalsIgnoreCase('modifiable')) {
            source.options.loadComponents = SnapshotView.Components.MODIFIABLE
            log.info("Set loadComponents to $target.")
        } else
            log.warn("Invalid ClearCase component target '$target'. Expected 'all' or 'modifiable'.")
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
