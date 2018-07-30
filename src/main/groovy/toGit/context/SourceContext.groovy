package toGit.context

import toGit.migration.sources.MigrationSource

abstract class SourceContext implements Context {
    MigrationSource source
}
