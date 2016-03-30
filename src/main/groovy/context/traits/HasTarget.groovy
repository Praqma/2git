package context.traits

import context.base.Context
import migration.targets.MigrationTarget

trait HasTarget implements Context {
    MigrationTarget target
}
