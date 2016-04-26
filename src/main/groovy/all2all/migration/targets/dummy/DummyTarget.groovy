package all2all.migration.targets.dummy

import all2all.context.base.Context
import all2all.migration.targets.MigrationTarget

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
