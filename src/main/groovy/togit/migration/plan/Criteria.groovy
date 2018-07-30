package togit.migration.plan

abstract class Criteria {
    abstract boolean appliesTo(Snapshot snapshot, List<Snapshot> allSnapshots)
}
