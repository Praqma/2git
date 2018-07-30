package togit.context

import static ContextHelper.executeInContext

import org.slf4j.LoggerFactory
import togit.migration.MigrationManager

class AfterContext implements Context {

    final static LOG = LoggerFactory.getLogger(this.class)

    /**
     * Defines {@link togit.migration.plan.Action}s to execute after the migration
     * @param closure the closure defining the {@link togit.migration.plan.Action}s
     */
    void actions(@DslContext(ActionsContext) Closure closure) {
        LOG.debug('Registering post-migration actions')
        ActionsContext actionsContext = MigrationManager.instance.actionsContext
        executeInContext(closure, actionsContext)
        int amount = actionsContext.actions.size()
        MigrationManager.instance.plan.afters.addAll(actionsContext.actions)
        LOG.debug("Registered $amount post-migration actions")
    }
}
