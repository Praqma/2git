package all2all.migration.plan

class SnapshotPlan {
    Snapshot snapshot
    List<Extraction> extractions = []
    List<Action> actions = []

    def SnapshotPlan(Snapshot snapshot) {
        this.snapshot = snapshot
    }
}
