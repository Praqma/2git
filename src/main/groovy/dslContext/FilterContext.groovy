package dslContext

import dslContext.base.Context
import dslContext.base.DslContext
import groovy.util.logging.Slf4j
import migration.filter.Filter
import migration.filter.actions.Action
import migration.filter.criterias.Criteria
import migration.filter.extractions.Extraction
import dslContext.traits.TActionsContext
import dslContext.traits.TCriteriaContext
import dslContext.traits.TExtractionsContext

import static dslContext.ContextHelper.executeInContext

@Slf4j
class FilterContext implements Context, TActionsContext, TCriteriaContext, TExtractionsContext {
    Filter filter

    /**
     * Adds a child filter to the filter
     * @param closure the Filter configuration
     */
    def void filter(@DslContext(FilterContext) Closure closure){
        log.debug('Entering filter().')
        def filterContext = new FilterContext()
        executeInContext(closure, filterContext)
        filter.filters.add(filterContext.filter)
        log.trace('Added a child filter to the filter.')
        log.debug('Exiting filter().')
    }

    /**{@inheritDoc }*/
    def void addActions(ArrayList<Action> given) {
        filter.actions.addAll(given)
    }

    /**{@inheritDoc }*/
    def void addExtractions(ArrayList<Extraction> given) {
        filter.extractions.addAll(given)
    }

    /**{@inheritDoc }*/
    def void addCriteria(ArrayList<Criteria> given) {
        filter.criteria.addAll(given)
    }
}
