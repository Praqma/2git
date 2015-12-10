package dslContext

@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
import migration.filter.Filter

@Slf4j
class MigrationStepsContext {
    List<Filter> filters = []

    /**
     * Configures a Filter for this migration
     * @param closure the Filter configuration
     */
    def void filter(@DelegatesTo(FilterContext) Closure closure) {
        log.debug('Entering filter().')
        def filterContext = new FilterContext()
        def filterClosure = closure.rehydrate(filterContext, this, this)
        filterClosure.resolveStrategy = Closure.DELEGATE_ONLY
        filterClosure.run()
        filters.add(filterContext.filter)
        log.debug('Exiting filter().')
    }
}
