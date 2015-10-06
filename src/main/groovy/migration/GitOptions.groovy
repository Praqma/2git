package migration

import groovy.util.logging.Slf4j

@Slf4j
class GitOptions {
    List<String> ignore = ['*.updt', 'lost+found', 'view.dat']
    String user = 'migration'
    String email =  'migration@cctogit.net'

    def void ignore(String... args){
        log.debug('Entering ignore().')
        ignore.addAll(args)
        log.info('Expanded Git ignore to: {}', ignore)
        log.debug('Exiting ignore().')
    }

    def void user(String user){
        log.debug('Entering user().')
        this.user = user
        log.info('Set user to: {}', user)
        log.debug('Exiting user().')
    }

    def void email(String email){
        log.debug('Entering email().')
        this.email = email
        log.info('Set email to: {}', email)
        log.debug('Exiting email().')
    }
}
