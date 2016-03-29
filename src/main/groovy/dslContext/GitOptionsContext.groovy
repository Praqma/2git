package dslContext

import dslContext.base.Context
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
        log.trace('Configuring Git options.')
        log.debug('Exiting GitOptionsContext().')
    }

    /**
     * Adds given String arguments to the Git ignore file
     * @param args the String arguments to add
     */
    def void ignore(String... args) {
        log.debug('Entering ignore().')
        gitOptions.ignore.addAll(args)
        log.trace('Expanded Git ignore to: {}', gitOptions.ignore)
        log.debug('Exiting ignore().')
    }

    /**
     * Sets the Git user
     * @param user the user name
     */
    def void user(String user) {
        log.debug('Entering user().')
        gitOptions.user = user
        log.trace('Set user to: {}', user)
        log.debug('Exiting user().')
    }

    /**
     * Sets the Git user email
     * @param email the user email
     */
    def void email(String email) {
        log.debug('Entering email().')
        gitOptions.email = email
        log.trace('Set email to: {}', email)
        log.debug('Exiting email().')
    }

    /**
     * Sets the Git dir path
     */
    def void dir(String path) {
        log.debug('Entering dir().')
        gitOptions.dir = path
        log.trace('Set dir to: {}', path)
        log.debug('Exiting dir().')
    }

    /**
     * Sets the Git work tree path
     */
    def void workTree(String path) {
        log.debug('Entering workTree().')
        gitOptions.workTree = path
        log.trace('Set workTree to: {}', path)
        log.debug('Exiting workTree().')
    }
}
