package all2all.migration.plan

abstract class Criteria {
    def abstract boolean appliesTo(Snapshot snapshot)
}
