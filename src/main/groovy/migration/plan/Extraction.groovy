package migration.plan

import migration.sources.Snapshot

abstract class Extraction {
    def abstract HashMap<String, Object> extract(Snapshot snapshot)
}
