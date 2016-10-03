package toGit.context

import org.slf4j.LoggerFactory
import toGit.context.base.Context
import toGit.context.base.DslContext
import toGit.migration.plan.Filter

import static ContextHelper.executeInContext

class FiltersContext implements Context {

    final static log = LoggerFactory.getLogger(this.class)

    List<Filter> filters = []

    /**
     * Registers a new {@link Filter}
     * @param closure the Filter configuration
     */
    void filter(@DslContext(FilterContext) Closure closure) {
        log.debug("Entering filter block")
        def filterContext = new FilterContext()
        executeInContext(closure, filterContext)
        filters.add(filterContext.filter)
        log.debug("Exiting filter block")
    }
}
