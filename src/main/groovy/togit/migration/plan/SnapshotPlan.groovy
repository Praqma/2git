package togit.migration.plan

/**
 * Represents the migration plan for a single Snapshot
 */
class SnapshotPlan {
    final Snapshot snapshot
    final List<Extraction> extractions = []
    final List<Action> actions = []

    SnapshotPlan(Snapshot snapshot) {
        this.snapshot = snapshot
    }
}
