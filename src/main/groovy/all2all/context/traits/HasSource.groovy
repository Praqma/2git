package all2all.context.traits

import all2all.context.base.Context
import all2all.migration.sources.MigrationSource

trait HasSource implements Context {
    MigrationSource source
}
