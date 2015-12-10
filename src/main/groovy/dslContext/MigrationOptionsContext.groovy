package dslContext

@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
import migration.GitOptions
import migration.MigrationOptions

@Slf4j
class MigrationOptionsContext {
    MigrationOptions migrationOptions

    /**
     * MigrationOptionsContext constructor
     */
    public MigrationOptionsContext(){
        log.debug('Entering MigrationOptionsContext().')
        migrationOptions = new MigrationOptions()
        log.debug('Exiting MigrationOptionsContext().')
    }

    /**
     * Sets Git options for this migration
     * @param closure the Git options configurations
     */
    def void git(@DelegatesTo(value = GitOptions, strategy = Closure.DELEGATE_ONLY) Closure closure){
        log.debug('Entering git().')
        def gitOptionsContext = new GitOptionsContext()
        closure.rehydrate(gitOptionsContext, this, this).run()
        migrationOptions.gitOptions = gitOptionsContext.gitOptions
        log.info('Configured git options.')
        log.debug('Exiting git().')
    }
}
