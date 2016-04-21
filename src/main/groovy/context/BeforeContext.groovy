package context

import context.base.Context
import context.base.DslContext
import groovy.util.logging.Slf4j
import migration.Migrator

import static ContextHelper.executeInContext

/**
 * Defines {@link migration.plan.Action}s to execute before the migration
 * @param closure the closure defining the {@link migration.plan.Action}s
 */
@Slf4j
class BeforeContext implements Context {
    void actions(@DslContext(ActionsContext) Closure closure) {
        log.info("Registering befores...")
        def actionsContext = Migrator.instance.actionsContext
        executeInContext(closure, actionsContext)
        Migrator.instance.befores.addAll(actionsContext.actions)
    }
}
