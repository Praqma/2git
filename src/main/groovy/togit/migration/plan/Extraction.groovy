package togit.migration.plan

abstract class Extraction {
    abstract HashMap<String, Object> extract(Snapshot snapshot)
}
