package toGit.context.traits

import toGit.context.base.Context
import toGit.migration.sources.MigrationSource

trait HasSource implements Context {
    MigrationSource source
}
