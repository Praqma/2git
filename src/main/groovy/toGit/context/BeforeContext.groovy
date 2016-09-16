package toGit.context

import toGit.context.base.Context
import toGit.context.base.DslContext
import toGit.migration.MigrationManager
import groovy.util.logging.Log

import static toGit.context.ContextHelper.executeInContext

/**
 * Defines {@link toGit.migration.plan.Action}s to execute before the migration
 * @param closure the closure defining the {@link toGit.migration.plan.Action}s
 */
@Log
class BeforeContext implements Context {
    void actions(@DslContext(ActionsContext) Closure closure) {
        log.info("Registering befores...")
        def actionsContext = MigrationManager.instance.actionsContext
        executeInContext(closure, actionsContext)
        MigrationManager.instance.plan.befores.addAll(actionsContext.actions)
    }
}
