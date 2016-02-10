package migration.clearcase

import groovy.util.logging.Slf4j
import migration.filter.Filter

@Slf4j
class Stream {
    String name     // Stream name
    String target   // Target branch name
    List<Filter> filters = []

    /**
     * Stream constructor
     * @param name the Stream name
     * @param branch the branch branch name
     */
    public Stream(String name) {
        log.debug('Entering Stream().')
        this.name = name
        this.target = name
        log.debug('Exiting Stream().')
    }
}
