package toGit.migration.plan

abstract class Criteria {
    def abstract boolean appliesTo(Snapshot snapshot, List<Snapshot> allSnapshots)
}
