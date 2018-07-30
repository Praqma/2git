package toGit.context

import org.slf4j.LoggerFactory
import toGit.migration.MigrationManager

import static ContextHelper.executeInContext

class AfterContext implements Context {

    final static log = LoggerFactory.getLogger(this.class)

    /**
     * Defines {@link toGit.migration.plan.Action}s to execute after the migration
     * @param closure the closure defining the {@link toGit.migration.plan.Action}s
     */
    void actions(@DslContext(ActionsContext) Closure closure) {
        log.debug("Registering post-migration actions")
        def actionsContext = MigrationManager.instance.actionsContext
        executeInContext(closure, actionsContext)
        def amount = actionsContext.actions.size()
        MigrationManager.instance.plan.afters.addAll(actionsContext.actions)
        log.debug("Registered $amount post-migration actions")
    }
}
