package migration

@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
import utils.StringExtensions

@Slf4j
/**
 * Used to specify in which Vob elements should be migrated from
 */
class Vob {
    String name // The name of the Vob
    List<Component> components = [] // The Components in the Vob to migrate

    /**
     * Constructor for Vob
     * @param name The name of the Vob
     * @return A new Vob
     */
    def Vob(String name) {
        log.debug('Entering Vob().')
        def parseResult = StringExtensions.parseClearCaseName(name)
        this.name = parseResult.vob
        log.info('Vob {} registered for migration.', this.name)
        log.debug('Exiting Vob().')
    }

    /**
     * Adds a Component to the component for migration
     * @param name The name of the Component
     * @param closure The configuration of the Component
     */
    def void component(String name, @DelegatesTo(Component) Closure closure) {
        log.debug('Entering component().')
        def component = new Component(name)
        def componentClosure = closure.rehydrate(component, this, this)
        componentClosure.resolveStrategy = Closure.DELEGATE_ONLY
        componentClosure()
        components.add(component)
        log.info('Added Component {} to Component {}.', component.name, this.name)
        log.debug('Exiting component().')
    }
}
