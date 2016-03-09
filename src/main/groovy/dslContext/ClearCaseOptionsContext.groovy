package dslContext

import groovy.util.logging.Slf4j
import migration.ClearCaseOptions
import net.praqma.clearcase.ucm.view.SnapshotView

@Slf4j
class ClearCaseOptionsContext implements Context {
    ClearCaseOptions clearCaseOptions

    /**
     * GitOptionsContext constructor
     */
    public ClearCaseOptionsContext() {
        log.debug('Entering ClearCaseOptionsContext().')
        clearCaseOptions = new ClearCaseOptions()
        log.trace('Configuring ClearCase options.')
        log.debug('Exiting ClearCaseOptionsContext().')
    }

    /**
     * Adds given String arguments to the Git ignore file
     * @param args the String arguments to add
     */
    def void loadComponents(String target) {
        log.debug('Entering loadComponents().')
        if(target.equalsIgnoreCase('all'))
            clearCaseOptions.loadComponents = SnapshotView.Components.ALL
        else if (target.equalsIgnoreCase('modifiable'))
            clearCaseOptions.loadComponents = SnapshotView.Components.MODIFIABLE
        else
            log.warn("Invalid ClearCase component target '$target'. Expected 'all' or 'modifiable'.")
        log.debug('Exiting loadComponents().')
    }

    /**
     * Creates the migration stream in given project.
     * If not set, migration stream will be created as a child stream of the migration target.
     * @param projectName the project to create the migration stream in
     */
    def void migrationProject(String projectName) {
        log.debug('Entering migrationProject().')
        clearCaseOptions.migrationProject = projectName
        log.debug('Exiting migrationProject().')
    }

    /**
     * Boolean setting the migration stream to readOnly or not.
     * If set to true, migration stream will be created as read-only.
     * @param readOnly the migration stream's read-only state
     */
    def void readOnlyMigrationStream(boolean readOnly = true) {
        log.debug('Entering readOnlyMigrationStream().')
        clearCaseOptions.readOnlyMigrationStream = readOnly
        log.debug('Exiting readOnlyMigrationStream().')
    }

    /**
     * Sets the ClearCase view path
     */
    def void view(String path) {
        log.debug('Entering view().')
        clearCaseOptions.view = path
        log.trace('Set view to: {}', path)
        log.debug('Exiting view().')
    }

    /*
     * Integer for setting the amount of times the view directory structure is flattened
     * @param count How many times the view's child directories are emptied into the root
     */
    def void flattenView(int count = 1) {
        log.debug('Entering flattenView().')
        clearCaseOptions.flattenView = count
        log.debug('Exiting flattenView().')
    }
}
