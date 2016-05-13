package toGit.migration.plan

/**
 * Represents the migration plan for a single Snapshot
 */
class SnapshotPlan {
    Snapshot snapshot
    List<Extraction> extractions = []
    List<Action> actions = []

    def SnapshotPlan(Snapshot snapshot) {
        this.snapshot = snapshot
    }
}
