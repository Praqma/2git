package dslContext

import groovy.util.logging.Slf4j
import migration.clearcase.Component

import static dslContext.ContextHelper.executeInContext

@Slf4j
class MigrationContext implements Context {
    List<Closure> befores = [] // Closures to execute before migrations
    List<Component> components = [] // The Components to migrate
    List<Closure> afters = [] // Closures to execute before migrations

    /**
     * Adds a closure to execute after migration starts.
     * @param closure the closure to execute
     */
    def void after(Closure closure) {
        log.debug('Entering after().')
        afters.add(closure)
        log.trace('Added after closure.')
        log.debug('Exiting after().')
    }
    
    /**
     * Adds a closure to execute before migration starts.
     * @param closure the closure to execute
     */
    def void before(Closure closure) {
        log.debug('Entering before().')
        befores.add(closure)
        log.trace('Added before closure.')
        log.debug('Exiting before().')
    }
    
    /**
     * Adds a Component to the Vob
     * @param name the Component name
     * @param closure the ComponentContext configuration
     */
    def void component(String name, @DslContext(ComponentContext) Closure closure) {
        log.debug('Entering component().')
        def componentContext = new ComponentContext(name)
        executeInContext(closure, componentContext)
        components.add(componentContext.component)
        log.trace('Added Component {}.', componentContext.component.name)
        log.debug('Exiting component().')
    }
}
