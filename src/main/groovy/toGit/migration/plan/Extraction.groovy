package toGit.migration.plan

abstract class Extraction {
    def abstract HashMap<String, Object> extract(Snapshot snapshot)
}
