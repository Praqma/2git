package all2all.migration.targets.git

import all2all.context.base.Context
import all2all.migration.MigrationManager
import all2all.migration.targets.MigrationTarget
import all2all.migration.targets.git.actions.Setup
import all2all.migration.targets.git.context.GitActionsContext

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
