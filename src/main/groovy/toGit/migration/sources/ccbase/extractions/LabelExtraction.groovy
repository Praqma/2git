package toGit.migration.sources.ccbase.extractions
import toGit.migration.plan.Extraction
import toGit.migration.plan.Snapshot

class LabelExtraction extends Extraction {

    String key

    LabelExtraction(String key) {
        this.key = key;
    }

    @Override HashMap<String, Object> extract(Snapshot snapshot) {
        def map = [:]
        map.put(key, snapshot.identifier)
        return map
    }
}
