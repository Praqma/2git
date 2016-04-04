package migration.targets.git

import context.ActionsContext
import context.base.Context
import migration.Migrator
import migration.targets.MigrationTarget
import migration.targets.git.context.GitActionsContext

class GitTarget implements MigrationTarget {
    GitOptions options

    @Override
    void prepare() {
        if (options.defaultSetup)
            Migrator.instance.befores.add(GitActionsContext.setup(options))
    }

    @Override
    void cleanup() {
        GitUtil.call("clean -f") //TODO should this be default?
    }

    @Override
    Context withActions(ActionsContext actionsContext) {
        return actionsContext as GitActionsContext
    }

}
