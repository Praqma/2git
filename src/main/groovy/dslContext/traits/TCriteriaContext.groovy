package dslContext.traits

import dslContext.CriteriaContext
import groovy.util.logging.Slf4j
import migration.filter.criterias.Criteria

import static dslContext.ContextHelper.executeInContext

@Slf4j
trait TCriteriaContext {
    List<Criteria> criteria = []

    /**
     * Defines criteria
     * @param closure the Criteria configuration
     */
    def void criteria(@DelegatesTo(CriteriaContext) Closure closure) {
        log.debug('Entering before().')
        def criteriaContext = new CriteriaContext()
        executeInContext(closure, criteriaContext)
        addCriteria(criteriaContext.criteria)
        criteria.addAll(criteriaContext.criteria)
        log.trace('Added {} criteria.', criteriaContext.criteria.size())
        log.debug('Exiting criteria().')
    }

    /**
     * Registers the defined criteria
     * @param given the criteria to register
     */
    def void addCriteria(ArrayList<Criteria> given) {
        criteria.addAll(given)
    }
}
