package context

import context.base.Context
import context.base.DslContext
import migration.Migrator

import static ContextHelper.executeInContext

class MigrationContext implements Context {

    /**
     * Code block to configure {@link migration.plan.Action}s to be executed after the migration
     * @param closure the After configuration
     */
    void after(@DslContext(AfterContext) Closure closure) {
        executeInContext(closure, new AfterContext())
    }

    /**
     * Code block to configure {@link migration.plan.Action}s to be executed before the migration
     * @param closure the Before configuration
     */
    void before(@DslContext(BeforeContext) Closure closure) {
        executeInContext(closure, new BeforeContext())
    }

    /**
     * Configures {@link migration.plan.Filter}s for this migration
     * @param closure the Filter configuration
     */
    void filters(@DslContext(FiltersContext) Closure closure) {
        def filtersContext = new FiltersContext()
        executeInContext(closure, filtersContext)
        Migrator.instance.filters.addAll(filtersContext.filters)
    }
}
