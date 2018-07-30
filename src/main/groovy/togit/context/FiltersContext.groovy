package togit.context

import static ContextHelper.executeInContext

import org.slf4j.LoggerFactory
import togit.migration.plan.Filter

class FiltersContext implements Context {

    final static LOG = LoggerFactory.getLogger(this.class)

    final List<Filter> filters = []

    /**
     * Registers a new {@link Filter}
     * @param closure the Filter configuration
     */
    void filter(@DslContext(FilterContext) Closure closure) {
        LOG.debug('Entering filter block')
        FilterContext filterContext = new FilterContext()
        executeInContext(closure, filterContext)
        filters.add(filterContext.filter)
        LOG.debug('Exiting filter block')
    }
}
