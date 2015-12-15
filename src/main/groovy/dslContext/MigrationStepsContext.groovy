package dslContext

@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
import migration.filter.Filter

import static dslContext.ContextHelper.executeInContext

@Slf4j
class MigrationStepsContext implements Context {
    List<Filter> filters = []

    /**
     * Configures a Filter for this migration
     * @param closure the Filter configuration
     */
    def void filter(@DslContext(FilterContext) Closure closure) {
        log.debug('Entering filter().')
        def filterContext = new FilterContext()
        executeInContext(closure, filterContext)
        filters.add(filterContext.filter)
        log.debug('Exiting filter().')
    }
}
