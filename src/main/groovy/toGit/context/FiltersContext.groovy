package toGit.context

import toGit.context.base.Context
import toGit.context.base.DslContext
import toGit.migration.plan.Filter

import static ContextHelper.executeInContext

class FiltersContext implements Context {
    List<Filter> filters = []

    /**
     * Registers a new {@link Filter}
     * @param closure the Filter configuration
     */
    void filter(@DslContext(FilterContext) Closure closure) {
        def filterContext = new FilterContext()
        executeInContext(closure, filterContext)
        filters.add(filterContext.filter)
    }
}
