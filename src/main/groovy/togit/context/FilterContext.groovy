package togit.context

import org.slf4j.LoggerFactory
import togit.migration.MigrationManager
import togit.migration.plan.Filter

class FilterContext implements Context {

    final static LOG = LoggerFactory.getLogger(this.class)

    final Filter filter = new Filter()

    /**
     * Adds child {@link Filter}s to the current {@link Filter}.
     * @param closure the Filter configuration
     */
    void filter(@DslContext(FilterContext) Closure closure) {
        LOG.debug('Entering subfilter block')
        FilterContext filterContext = new FilterContext()
        ContextHelper.executeInContext(closure, filterContext)
        filter.filters.add(filterContext.filter)
        LOG.debug('Exiting subfilter block')
    }

    /**
     * Adds {@link togit.migration.plan.Action}s to the current {@link Filter}.
     * @param closure the Actions configuration
     */
    void actions(@DslContext(ActionsContext) Closure closure) {
        LOG.debug('Entering actions block')
        ActionsContext actionsContext = MigrationManager.instance.actionsContext
        ContextHelper.executeInContext(closure, actionsContext)
        filter.actions.addAll(actionsContext.actions)
        LOG.debug('Exiting actions block')
    }

    /**
     * Adds {@link togit.migration.plan.Extraction}s to the current {@link Filter}.
     * @param closure the Extractions configuration
     */
    void extractions(@DslContext(ExtractionsContext) Closure closure) {
        LOG.debug('Entering extractions block')
        ExtractionsContext extractionsContext = MigrationManager.instance.extractionsContext
        ContextHelper.executeInContext(closure, extractionsContext)
        filter.extractions.addAll(extractionsContext.extractions)
        LOG.debug('Exiting extractions block')
    }

    /**
     * Adds {@link togit.migration.plan.Criteria} to the current {@link Filter}.
     * @param closure the Criteria configuration
     */
    void criteria(@DslContext(CriteriaContext) Closure closure) {
        LOG.debug('Entering criteria block')
        CriteriaContext criteriaContext = MigrationManager.instance.criteriaContext
        ContextHelper.executeInContext(closure, criteriaContext)
        filter.criteria.addAll(criteriaContext.criteria)
        LOG.debug('Exiting criteria block')
    }
}
