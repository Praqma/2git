package dslContext

@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
import migration.clearcase.Vob

import static dslContext.ContextHelper.executeInContext

@Slf4j
class MigrationContext implements Context {
    List<Vob> vobs = []

    /**
     * Adds a Vob to the component for migration
     * @param name The name of the Vob
     * @param closure The configuration of the Vob
     */
    def void vob(String name, @DslContext(VobContext) Closure closure) {
        log.debug('Entering vob().')
        def vobContext = new VobContext(name)
        executeInContext(closure, vobContext)
        vobs.add(vobContext.vob)
        log.trace('Added Vob {}.', vobContext.vob.name)
        log.debug('Exiting vob().')
    }
}
