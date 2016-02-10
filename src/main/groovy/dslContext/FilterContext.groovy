package dslContext

import groovy.util.logging.Slf4j
import migration.filter.Filter

import static dslContext.ContextHelper.executeInContext

@Slf4j
class FilterContext implements Context {
    Filter filter

    /**
     * FilterContext constructor
     * @return a new FilterContext
     */
    def FilterContext() {
        log.debug('Entering FilterContext().')
        filter = new Filter()
        log.trace("Creating a filter for migration.")
        log.debug('Exiting FilterContext().')
    }

    /**
     * Sets the Filter Actions
     * @param closure the Actions configuration
     */
    def void actions(@DelegatesTo(ActionsContext) Closure closure) {
        log.debug('Entering actions().')
        def actionsContext = new ActionsContext()
        executeInContext(closure, actionsContext)
        filter.actions.addAll(actionsContext.actions)
        log.trace('Added {} actions to the filter.', actionsContext.actions.size())
        log.debug('Exiting actions().')
    }

    /**
     * Sets the Filter Criteria
     * @param closure the Criteria configuration
     */
    def void criteria(@DslContext(CriteriaContext) Closure closure) {
        log.debug('Entering criteria().')
        def criteriaContext = new CriteriaContext()
        executeInContext(closure, criteriaContext)
        filter.criteria.addAll(criteriaContext.criteria)
        log.trace('Added {} criteria to the filter.', criteriaContext.criteria.size())
        log.debug('Exiting criteria().')
    }

    /**
     * Sets the Filter Extractions
     * @param closure the Extractions configuration
     */
    def void extractions(@DslContext(ExtractionsContext) Closure closure) {
        log.debug('Entering extractions().')
        def extractionsContext = new ExtractionsContext()
        executeInContext(closure, extractionsContext)
        filter.extractions.addAll(extractionsContext.extractions)
        log.trace('Added {} extractions to the filter.', extractionsContext.extractions.size())
        log.debug('Exiting extractions().')
    }

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
}
