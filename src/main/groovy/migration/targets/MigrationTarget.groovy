package migration.targets

import context.ActionsContext

interface MigrationTarget {
    void prepare()

    void cleanup()

    ActionsContext withActions(ActionsContext actionsContext)
}