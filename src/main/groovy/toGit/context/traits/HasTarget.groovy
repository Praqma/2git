package toGit.context.traits

import toGit.context.base.Context
import toGit.migration.targets.MigrationTarget

trait HasTarget implements Context {
    MigrationTarget target
}
