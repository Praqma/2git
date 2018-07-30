package toGit.context

import toGit.migration.targets.MigrationTarget

abstract class TargetContext implements Context {
    MigrationTarget target
}
