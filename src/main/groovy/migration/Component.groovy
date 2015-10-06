package migration


@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
import org.apache.commons.lang.NotImplementedException
import utils.StringExtensions

@Slf4j
/**
 * Used for registering a Component for migration
 */
class Component {
    String name     // The name of the Component
    List<Stream> streams = []   // The Streams for which this Component should be migrated
    MigrationOptions migrationOptions   // Contains some options used in the migration

    /**
     * Constructor for Component
     * @param name The name of the Component
     * @return A new Component
     */
    def Component(String name) {
        log.debug('Entering Component().')
        def parseResult = StringExtensions.parseClearCaseName(name)
        this.name = parseResult.tag
        log.info("Component {} registered for migration.", this.name)
        log.debug('Exiting Component().')
    }

    /**
     * Adds a Stream to the component for migration
     * @param name The name of the Stream
     * @param closure The configuration of the Stream
     */
    def void stream(String name, @DelegatesTo(Stream) Closure closure) {
        log.debug('Entering stream().')
        if (streams) throw new NotImplementedException("Multiple streams for one component aren't supported yet.")
        def stream = new Stream(name)
        def streamClosure = closure.rehydrate(stream, this, this)
        streamClosure.resolveStrategy = Closure.DELEGATE_ONLY
        streamClosure()
        streams.add(stream)
        log.info('Added Stream {} to Component {}.', stream.name, this.name)
        log.debug('Exiting stream().')
    }
    
    def void migrationOptions(@DelegatesTo(MigrationOptions) Closure closure){
        log.debug('Entering migrationOptions().')
        def migrationOptions = new MigrationOptions()
        def migrationOptionsClosure = closure.rehydrate(migrationOptions, this, this)
        migrationOptionsClosure.resolveStrategy = Closure.DELEGATE_ONLY
        migrationOptionsClosure()
        this.migrationOptions = migrationOptions
        log.info('Configured migration options for Component {}.', this.name)
        log.debug('Exiting migrationOptions().')
    }
}
