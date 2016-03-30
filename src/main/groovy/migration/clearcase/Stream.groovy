package migration.clearcase

import groovy.util.logging.Slf4j
import migration.filter.Filter
import utils.StringExtensions

@Slf4j
class Stream {
    String name     // Stream name
    String vob      // Vob name
    String target   // Target branch name
    List<Filter> filters = []

    /**
     * Stream constructor
     * @param name the Stream name
     * @param branch the branch branch name
     */
    public Stream(String name) {
        log.debug('Entering Stream().')
        if(!StringExtensions.isFullyQualifiedName(name))
            throw new Exception("Failed to parse $name as fully qualified name.")
        def map = StringExtensions.parseClearCaseName(name)
        this.name = map.tag
        this.target = map.tag
        this.vob = map.vob
        log.debug('Exiting Stream().')
    }
}
