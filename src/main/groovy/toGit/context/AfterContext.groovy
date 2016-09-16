package toGit.context

import groovy.util.logging.Log
import toGit.context.base.Context
import toGit.context.base.DslContext
import toGit.migration.MigrationManager

import static ContextHelper.executeInContext

@Log
class AfterContext implements Context {

    /**
     * Defines {@link toGit.migration.plan.Action}s to execute after the migration
     * @param closure the closure defining the {@link toGit.migration.plan.Action}s
     */
    void actions(@DslContext(ActionsContext) Closure closure) {
        log.info("Registering afters...")
        def actionsContext = MigrationManager.instance.actionsContext
        executeInContext(closure, actionsContext)
        MigrationManager.instance.plan.afters.addAll(actionsContext.actions)
    }
}
