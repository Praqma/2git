package dslContext

@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
import migration.MigrationOptions

import static dslContext.ContextHelper.executeInContext

@Slf4j
class MigrationOptionsContext implements Context {
    MigrationOptions migrationOptions

    /**
     * MigrationOptionsContext constructor
     */
    public MigrationOptionsContext() {
        log.debug('Entering MigrationOptionsContext().')
        migrationOptions = new MigrationOptions()
        log.debug('Exiting MigrationOptionsContext().')
    }

    /**
     * Sets Git options for this migration
     * @param closure the Git options configurations
     */
    def void git(@DslContext(GitOptionsContext) Closure closure) {
        log.debug('Entering git().')
        def gitOptionsContext = new GitOptionsContext()
        executeInContext(closure, gitOptionsContext)
        migrationOptions.gitOptions = gitOptionsContext.gitOptions
        log.trace('Configured git options.')
        log.debug('Exiting git().')
    }
}
