package toGit.migration.targets.dummy

import toGit.context.base.Context
import toGit.migration.targets.MigrationTarget

class DummyTarget implements MigrationTarget {
    @Override
    void prepare() {

    }

    @Override
    void cleanup() {

    }

    @Override
    Context withActions(Context actionsContext) {
        return actionsContext
    }
}
