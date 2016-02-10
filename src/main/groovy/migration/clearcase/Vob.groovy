package migration.clearcase

import groovy.util.logging.Slf4j

@Slf4j
/**
 * Used to specify in which Vob elements should be migrated from
 */
class Vob {
    String name // The name of the Vob
    List<Component> components = [] // The Components in the Vob to migrate

    /**
     * Vob constructor
     * @param name the Vob name
     */
    public Vob(String name) {
        log.debug('Entering Vob()')
        this.name = name
        log.debug('Exiting Vob()')
    }
}
