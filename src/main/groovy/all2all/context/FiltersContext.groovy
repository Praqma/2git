package all2all.context

import all2all.context.base.Context
import all2all.context.base.DslContext
import all2all.migration.plan.Filter

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
