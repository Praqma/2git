package dslContext

@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
import migration.clearcase.Component
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import utils.StringExtensions

import static dslContext.ContextHelper.executeInContext

@Slf4j
class ComponentContext implements Context {
    Component component

    /**
     * ComponentContext constructor
     * @param name the Component name
     */
    public ComponentContext(String name) {
        log.debug('Entering ComponentContext().')
        def parseResult = StringExtensions.parseClearCaseName(name)
        component = new Component(parseResult.tag)
        log.info("Component {} registered for migration.", component.name)
        log.debug('Exiting ComponentContext().')
    }

    /**
     * Adds a Stream to the component for migration
     * @param name The name of the Stream
     * @param closure The configuration of the Stream
     */
    def void stream(String name, @DslContext(StreamContext) Closure closure) {
        log.debug('Entering stream().')
        if (component.streams) throw new NotImplementedException("Multiple streams for one component aren't supported yet.") //TODO Isn't it?
        def streamContext = new StreamContext(name)
        executeInContext(closure, streamContext)
        component.streams.add(streamContext.stream)
        log.info('Added Stream {} to Component {}.', streamContext.stream.name, component.name)
        log.debug('Exiting stream().')
    }

    /**
     * Sets the branch repository for the component
     * @param path the repository path
     */
    def void repository(String repository) {
        log.debug('Entering repository().')
        component.target = repository
        log.info('Set Component {} repository path to {}', component.name, component.target)
        log.debug('Exiting repository().')
    }

    /**
     * Sets migration options for the Component
     * @param closure the migration options to set
     */
    def void migrationOptions(@DslContext(MigrationOptionsContext) Closure closure) {
        log.debug('Entering migrationOptions().')
        def migrationOptionsContext = new MigrationOptionsContext()
        executeInContext(closure, migrationOptionsContext)
        component.migrationOptions = migrationOptionsContext.migrationOptions
        log.info('Configured migration options for Component {}.', component.name)
        log.debug('Exiting migrationOptions().')
    }
}
