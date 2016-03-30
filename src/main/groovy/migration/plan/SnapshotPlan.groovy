package migration.plan

import migration.sources.Snapshot

class SnapshotPlan {
    Snapshot snapshot
    List<Extraction> extractions = []
    List<Action> actions = []

    def SnapshotPlan(Snapshot snapshot) {
        this.snapshot = snapshot
    }

    boolean matches(ArrayList<Criteria> criteria) {
        for (def crit : criteria) {
            if (!crit.appliesTo(snapshot))
                return false
        }
        return true
    }
}
