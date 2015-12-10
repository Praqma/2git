package dslContext

@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
import migration.filter.Filter

@Slf4j

class FilterContext {
    Filter filter

    /**
     * FilterContext constructor
     * @return a new FilterContext
     */
    def FilterContext(){
        log.debug('Entering FilterContext().')
        filter = new Filter()
        log.debug('Exiting FilterContext().')
    }

    /**
     * Sets the Filter Criteria
     * @param closure the Criteria configuration
     */
    def void criteria(@DelegatesTo(value = CriteriaContext, strategy = Closure.DELEGATE_ONLY) Closure closure) {
        log.debug('Entering criteria().')
        def criteriaContext = new CriteriaContext()
        closure.rehydrate(criteriaContext, this, this).run()
        filter.criteria = criteriaContext.criteria
        log.info('Added {} Criteria to Filter.', criteriaContext.criteria.size())
        log.debug('Exiting criteria().')
    }

    /**
     * Sets the Filter Extractions
     * @param closure the Extractions configuration
     */
    def void extractions(@DelegatesTo(value = ExtractionsContext, strategy = Closure.DELEGATE_ONLY) Closure closure) {
        log.debug('Entering extractions().')
        def extractionsContext = new ExtractionsContext()
        closure.rehydrate(extractionsContext, this, this).run()
        filter.extractions = extractionsContext.extractions
        log.info('Added {} Extractions to Filter.', extractionsContext.extractions.size())
        log.debug('Exiting extractions().')
    }

    /**
     * Sets the Filter Actions
     * @param closure the Actions configuration
     */
    def void actions(@DelegatesTo(value = ActionsContext, strategy = Closure.DELEGATE_ONLY) Closure closure) {
        log.debug('Entering actions().')
        def actionsContext = new ActionsContext()
        closure.rehydrate(actionsContext, this, this).run()
        filter.actions = actionsContext.actions
        log.info('Added {} Actions to Filter.', actionsContext.actions.size())
        log.debug('Exiting actions().')
    }
}
