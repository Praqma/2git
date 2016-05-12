package toGit.migration.targets.git

import toGit.context.base.Context
import toGit.migration.MigrationManager
import toGit.migration.targets.MigrationTarget
import toGit.migration.targets.git.actions.Setup
import toGit.migration.targets.git.context.GitActionsContext

class GitTarget implements MigrationTarget {
    GitOptions options

    @Override
    void prepare() {
        if (options.defaultSetup)
            MigrationManager.instance.plan.befores.add(new Setup(new File(workspace), options))
    }

    @Override
    void cleanup() {

    }

    @Override
    Context withActions(Context actionsContext) {
        return actionsContext as GitActionsContext
    }

}
