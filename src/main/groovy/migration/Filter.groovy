package migration

@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j

@Slf4j
class Filter {
    List<Criteria> criteria = []
    List<Extraction> extractions = []
    List<Action> actions = []

    def void criteria(@DelegatesTo(CriteriaContext) Closure closure) {
        log.debug('Entering criteria().')
        def criteriaContext = new CriteriaContext()
        def criteriaClosure = closure.rehydrate(criteriaContext, this, this)
        criteriaClosure.resolveStrategy = Closure.DELEGATE_ONLY
        criteriaClosure()
        criteria = criteriaContext.criteria
        log.info('Added {} Criteria to Filter.', criteria.size())
        log.debug('Exiting criteria().')
    }

    def void extractions(@DelegatesTo(ExtractionsContext) Closure closure) {
        log.debug('Entering extractions().')
        def extractionsContext = new ExtractionsContext()
        def extractionsClosure = closure.rehydrate(extractionsContext, this, this)
        extractionsClosure.resolveStrategy = Closure.DELEGATE_ONLY
        extractionsClosure()
        extractions = extractionsContext.extractions
        log.info('Added {} Extractions to Filter.', extractions.size())
        log.debug('Exiting extractions().')
    }

    def void actions(@DelegatesTo(ActionsContext) Closure closure) {
        log.debug('Entering actions().')
        def actionsContext = new ActionsContext()
        def actionsClosure = closure.rehydrate(actionsContext, this, this)
        actionsClosure.resolveStrategy = Closure.DELEGATE_ONLY
        actionsClosure()
        actions = actionsContext.actions
        log.info('Added {} Actions to Filter.', actions.size())
        log.debug('Exiting actions().')
    }
}
