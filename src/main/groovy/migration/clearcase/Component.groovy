package migration.clearcase

@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
import migration.MigrationOptions

@Slf4j
class Component {
    String name     // Component name

    List<Stream> streams = []   // Component Streams to migrate
    MigrationOptions migrationOptions   // Some options used in the migration

    /**
     * Component constructor
     * @param name the Component name
     */
    public Component(String name) {
        log.debug('Entering Component().')
        this.name = name
        log.debug('Exiting Component().')
    }
}
