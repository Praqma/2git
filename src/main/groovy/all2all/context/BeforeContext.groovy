package all2all.context

import all2all.context.base.Context
import all2all.context.base.DslContext
import all2all.migration.MigrationManager
import groovy.util.logging.Slf4j

/**
 * Defines {@link all2all.migration.plan.Action}s to execute before the migration
 * @param closure the closure defining the {@link all2all.migration.plan.Action}s
 */
@Slf4j
class BeforeContext implements Context {
    void actions(@DslContext(ActionsContext) Closure closure) {
        log.info("Registering befores...")
        def actionsContext = MigrationManager.instance.actionsContext
        executeInContext(closure, actionsContext)
        MigrationManager.instance.plan.befores.addAll(actionsContext.actions)
    }
}
