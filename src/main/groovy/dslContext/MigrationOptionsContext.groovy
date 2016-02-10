package dslContext

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

    /**
     * Sets ClearCase options for this migration
     * @param closure the ClearCase options configurations
     */
    def void clearCase(@DslContext(ClearCaseOptionsContext) Closure closure) {
        log.debug('Entering clearCase().')
        def clearCaseOptionsContext = new ClearCaseOptionsContext()
        executeInContext(closure, clearCaseOptionsContext)
        migrationOptions.clearCaseOptions = clearCaseOptionsContext.clearCaseOptions
        log.trace('Configured ClearCase options.')
        log.debug('Exiting clearCase().')
    }
}
