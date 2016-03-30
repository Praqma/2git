package migration.clearcase

import groovy.util.logging.Slf4j
import migration.MigrationOptions
import utils.StringExtensions

@Slf4j
class Component {
    String name     // Component name
    String vobName      // Vob name
    List<Stream> streams = []   // Component Streams to migrate
    MigrationOptions migrationOptions = new MigrationOptions()   // Some options used in the migration

    /**
     * Component constructor
     * @param name the Component name
     */
    public Component(String name) {
        log.debug('Entering Component().')
        if(!StringExtensions.isFullyQualifiedName(name))
            throw new Exception("Failed to parse $name as fully qualified name.")
        def map = StringExtensions.parseClearCaseName(name)
        this.name = map.tag
        this.vobName = map.vob
        log.debug('Exiting Component().')
    }
}
