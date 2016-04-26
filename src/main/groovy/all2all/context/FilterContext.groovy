package all2all.context

import all2all.context.base.Context
import all2all.context.base.DslContext
import all2all.migration.MigrationManager
import all2all.migration.plan.Filter

class FilterContext implements Context {
    Filter filter = new Filter()

    /**
     * Adds child {@link Filter}s to the current {@link Filter}.
     * @param closure the Filter configuration
     */
    void filter(@DslContext(FilterContext) Closure closure) {
        def filterContext = new FilterContext()
        ContextHelper.executeInContext(closure, filterContext)
        filter.filters.add(filterContext.filter)
    }

    /**
     * Adds {@link all2all.migration.plan.Action}s to the current {@link Filter}.
     * @param closure the Actions configuration
     */
    void actions(@DslContext(ActionsContext) Closure closure) {
        def actionsContext = MigrationManager.instance.actionsContext
        ContextHelper.executeInContext(closure, actionsContext)
        filter.actions.addAll(actionsContext.actions)
    }

    /**
     * Adds {@link all2all.migration.plan.Extraction}s to the current {@link Filter}.
     * @param closure the Extractions configuration
     */
    void extractions(@DslContext(ExtractionsContext) Closure closure) {
        def extractionsContext = MigrationManager.instance.extractionsContext
        ContextHelper.executeInContext(closure, extractionsContext)
        filter.extractions.addAll(extractionsContext.extractions)
    }

    /**
     * Adds {@link all2all.migration.plan.Criteria} to the current {@link Filter}.
     * @param closure the Criteria configuration
     */
    void criteria(@DslContext(CriteriaContext) Closure closure) {
        def criteriaContext = MigrationManager.instance.criteriaContext
        ContextHelper.executeInContext(closure, criteriaContext)
        filter.criteria.addAll(criteriaContext.criteria)
    }
}
