package togit.context

import togit.migration.targets.MigrationTarget

abstract class TargetContext implements Context {
    MigrationTarget target
}
