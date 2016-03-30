package context

import context.base.Context
import context.base.DslContext
import migration.Migrator

import static ContextHelper.executeInContext

class AfterContext implements Context {

    /**
     * Defines {@link migration.plan.Action}s to execute after the migration
     * @param closure the closure defining the {@link migration.plan.Action}s
     */
    void actions(@DslContext(ActionsContext) Closure closure) {
        def actionsContext = Migrator.instance.actionsContext
        executeInContext(closure, actionsContext)
        Migrator.instance.afters.addAll(actionsContext.actions)
    }
}
