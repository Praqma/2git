package migration.sources

abstract class Snapshot {
    String identifier

    public Snapshot(String identifier) {
        this.identifier = identifier
    }
}
