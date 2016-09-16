package toGit.context.traits

import toGit.context.base.Context
import toGit.migration.targets.MigrationTarget

trait TargetContext implements Context {
    MigrationTarget target
}
