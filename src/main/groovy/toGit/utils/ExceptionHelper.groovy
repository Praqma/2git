package toGit.utils

import groovy.util.logging.Log

@Log
class ExceptionHelper {
    static void simpleLog(Exception e) {
        while(e){
            def name = e.class.simpleName
            def message = e.message
            log.severe("$name: $message")
            e = e.cause instanceof Exception ? e.cause as Exception : null
        }
    }
}
