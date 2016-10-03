package toGit.context

import org.slf4j.LoggerFactory
import toGit.context.base.Context
import toGit.context.base.DslContext
import toGit.migration.MigrationManager

import static toGit.context.ContextHelper.executeInContext

/**
 * Defines {@link toGit.migration.plan.Action}s to execute before the migration
 * @param closure the closure defining the {@link toGit.migration.plan.Action}s
 */
class BeforeContext implements Context {

    final static log = LoggerFactory.getLogger(this.class)

    void actions(@DslContext(ActionsContext) Closure closure) {
        log.debug("Registering pre-migration actions")
        def actionsContext = MigrationManager.instance.actionsContext
        executeInContext(closure, actionsContext)
        def amount = actionsContext.actions.size()
        MigrationManager.instance.plan.befores.addAll(actionsContext.actions)
        log.debug("Registered $amount pre-migration actions")
    }
}
