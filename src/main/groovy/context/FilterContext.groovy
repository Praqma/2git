package context

import context.base.Context
import context.base.DslContext
import migration.Migrator
import migration.plan.Filter

class FilterContext implements Context {
    Filter filter

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
     * Adds {@link migration.plan.Action}s to the current {@link Filter}.
     * @param closure the Actions configuration
     */
    void actions(@DslContext(ActionsContext) Closure closure) {
        def actionsContext = Migrator.instance.actionsContext
        ContextHelper.executeInContext(closure, actionsContext)
        filter.actions.addAll(actionsContext.actions)
    }

    /**
     * Adds {@link migration.plan.Extraction}s to the current {@link Filter}.
     * @param closure the Extractions configuration
     */
    void extractions(@DslContext(ExtractionsContext) Closure closure) {
        def extractionsContext = Migrator.instance.extractionsContext
        ContextHelper.executeInContext(closure, extractionsContext)
        filter.extractions.addAll(extractionsContext.extractions)
    }

    /**
     * Adds {@link migration.plan.Criteria} to the current {@link Filter}.
     * @param closure the Criteria configuration
     */
    void criteria(@DslContext(CriteriaContext) Closure closure) {
        def criteriaContext = Migrator.instance.criteriaContext
        ContextHelper.executeInContext(closure, criteriaContext)
        filter.criteria.addAll(criteriaContext.criteria)
    }
}
