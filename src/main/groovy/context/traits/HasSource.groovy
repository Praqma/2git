package context.traits

import context.base.Context
import migration.sources.MigrationSource

trait HasSource implements Context {
    MigrationSource source
}
