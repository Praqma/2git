package migration.plan

import migration.sources.Snapshot

class SnapshotPlan {
    Snapshot snapshot
    List<Extraction> extractions = []
    List<Action> actions = []

    def SnapshotPlan(Snapshot snapshot) {
        this.snapshot = snapshot
    }
}
