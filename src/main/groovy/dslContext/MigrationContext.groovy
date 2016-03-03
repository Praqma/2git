package dslContext

import groovy.util.logging.Slf4j
import migration.clearcase.Component

import static dslContext.ContextHelper.executeInContext

@Slf4j
class MigrationContext implements Context {
    List<Component> components = [] // The Components to migrate

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
