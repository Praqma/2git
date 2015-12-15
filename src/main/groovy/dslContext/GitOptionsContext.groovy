package dslContext

@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
import migration.GitOptions

@Slf4j
class GitOptionsContext implements Context {
    GitOptions gitOptions

    /**
     * GitOptionsContext constructor
     */
    public GitOptionsContext() {
        log.debug('Entering GitOptionsContext().')
        gitOptions = new GitOptions()
        log.debug('Exiting GitOptionsContext().')
    }

    /**
     * Adds given String arguments to the Git ignore file
     * @param args the String arguments to add
     */
    def void ignore(String... args) {
        log.debug('Entering ignore().')
        gitOptions.ignore.addAll(args)
        log.info('Expanded Git ignore to: {}', gitOptions.ignore)
        log.debug('Exiting ignore().')
    }

    /**
     * Sets the Git user
     * @param user the user name
     */
    def void user(String user) {
        log.debug('Entering user().')
        gitOptions.user = user
        log.info('Set user to: {}', user)
        log.debug('Exiting user().')
    }

    /**
     * Sets the Git user email
     * @param email the user email
     */
    def void email(String email) {
        log.debug('Entering email().')
        gitOptions.email = email
        log.info('Set email to: {}', email)
        log.debug('Exiting email().')
    }
}
