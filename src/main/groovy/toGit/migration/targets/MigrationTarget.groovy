package toGit.migration.targets

import toGit.context.base.Context

trait MigrationTarget {
    String workspace = new File("./output/target").absolutePath

    abstract void prepare()

    abstract void cleanup()

    abstract Context withActions(Context actionsContext)
}