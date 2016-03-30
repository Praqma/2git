package dslContext.traits

import dslContext.ActionsContext
import groovy.util.logging.Slf4j
import migration.filter.actions.Action

import static dslContext.ContextHelper.executeInContext

@Slf4j
trait HasActions {
    List<Action> actions = []

    /**
     * Define actions to register
     * @param closure the Actions configuration
     */
    def void actions(@DelegatesTo(ActionsContext) Closure closure) {
        log.debug('Entering actions().')
        def actionsContext = new ActionsContext()
        executeInContext(closure, actionsContext)
        addActions(actionsContext.actions)
        log.trace('Added {} actions.', actionsContext.actions.size())
        log.debug('Exiting actions().')
    }

    /**
     * Registers the defined actions
     * @param given the actions to register
     */
    def void addActions(ArrayList<Action> given) {
        actions.addAll(given)
    }
}
