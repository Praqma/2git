package migration.targets

import context.ActionsContext
import context.base.Context

interface MigrationTarget {
    void prepare()

    void cleanup()

    Context withActions(ActionsContext actionsContext)
}