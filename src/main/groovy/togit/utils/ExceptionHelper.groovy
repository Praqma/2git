package togit.utils

import org.slf4j.LoggerFactory

class ExceptionHelper {

    final static LOG = LoggerFactory.getLogger(this.class)

    static void simpleLog(Exception e) {
        while (e) {
            String name = e.class.simpleName
            String message = e.message
            LOG.error("$name: $message")
            e = e as Exception
        }
    }
}
