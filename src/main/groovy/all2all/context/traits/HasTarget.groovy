package all2all.context.traits

import all2all.context.base.Context
import all2all.migration.targets.MigrationTarget

trait HasTarget implements Context {
    MigrationTarget target
}
