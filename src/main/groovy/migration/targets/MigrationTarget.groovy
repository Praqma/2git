package migration.targets

import context.ActionsContext
import context.base.Context

trait MigrationTarget {
    String dir = new File("./output/target").absolutePath

    abstract void prepare()

    abstract void cleanup()

    abstract Context withActions(ActionsContext actionsContext)
}