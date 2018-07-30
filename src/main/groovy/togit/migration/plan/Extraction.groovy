package togit.migration.plan

abstract class Extraction {
    abstract Map<String, Object> extract(Snapshot snapshot)
}
