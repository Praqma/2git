package all2all.migration.plan

abstract class Extraction {
    def abstract HashMap<String, Object> extract(Snapshot snapshot)
}
