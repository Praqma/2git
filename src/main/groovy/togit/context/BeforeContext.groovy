package togit.context

import static togit.context.ContextHelper.executeInContext

import org.slf4j.LoggerFactory
import togit.migration.MigrationManager

/**
 * Defines {@link togit.migration.plan.Action}s to execute before the migration
 * @param closure the closure defining the {@link togit.migration.plan.Action}s
 */
class BeforeContext implements Context {

    final static LOG = LoggerFactory.getLogger(this.class)

    void actions(@DslContext(ActionsContext) Closure closure) {
        LOG.debug('Registering pre-migration actions')
        ActionsContext actionsContext = MigrationManager.instance.actionsContext
        executeInContext(closure, actionsContext)
        int amount = actionsContext.actions.size()
        MigrationManager.instance.plan.befores.addAll(actionsContext.actions)
        LOG.debug("Registered $amount pre-migration actions")
    }
}
