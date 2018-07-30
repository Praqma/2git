package togit.context

import togit.migration.sources.MigrationSource

abstract class SourceContext implements Context {
    MigrationSource source
}
