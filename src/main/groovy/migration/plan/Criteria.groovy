package migration.plan

import migration.sources.Snapshot

abstract class Criteria {
    def abstract boolean appliesTo(Snapshot snapshot)
}
