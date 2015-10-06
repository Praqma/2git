package migration

import groovy.util.logging.Slf4j

@Slf4j
class MigrationOptions {
    GitOptions gitOptions

    def void git(@DelegatesTo(GitOptions) Closure closure){
        log.debug('Entering gitOptions().')
        def gitOptions = new GitOptions()
        def gitOptionsClosure = closure.rehydrate(gitOptions, this, this)
        gitOptionsClosure.resolveStrategy = Closure.DELEGATE_ONLY
        gitOptionsClosure()
        this.gitOptions = gitOptions
        log.info('Configured git options.')
        log.debug('Exiting gitOptions().')
    }
}
