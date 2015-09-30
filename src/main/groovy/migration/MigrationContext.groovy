package migration

@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j

@Slf4j
class MigrationContext {
    List<Vob> vobs = []

    /**
     * Adds a Vob to the component for migration
     * @param name The name of the Vob
     * @param closure The configuration of the Vob
     */
    def void vob(String name, @DelegatesTo(Vob) Closure closure) {
        log.debug('Entering vob().')
        def vob = new Vob(name)
        def vobClosure = closure.rehydrate(vob, this, this)
        vobClosure.resolveStrategy = Closure.DELEGATE_ONLY
        vobClosure()
        vobs.add(vob)
        log.info('Added Vob {}.', vob.name)
        log.debug('Exiting vob().')
    }
}
