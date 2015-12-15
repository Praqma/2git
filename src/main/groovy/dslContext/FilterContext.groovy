package dslContext

@Grab('org.slf4j:slf4j-simple:1.7.7')
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
        log.debug('Exiting FilterContext().')
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
        log.info('Added {} Criteria to Filter.', criteriaContext.criteria.size())
        log.debug('Exiting criteria().')
    }

    /**
     * Sets the Filter Extractions
     * @param closure the Extractions configuration
     */
    def void extractions(@DelegatesTo(ExtractionsContext) Closure closure) {
        log.debug('Entering extractions().')
        def extractionsContext = new ExtractionsContext()
        executeInContext(closure, extractionsContext)
        filter.extractions.addAll(extractionsContext.extractions)
        log.info('Added {} Extractions to Filter.', extractionsContext.extractions.size())
        log.debug('Exiting extractions().')
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
        log.info('Added {} Actions to Filter.', actionsContext.actions.size())
        log.debug('Exiting actions().')
    }
}
