package toGit.context.traits

import toGit.context.base.Context
import toGit.migration.sources.MigrationSource

trait SourceContext implements Context {
    MigrationSource source
}
