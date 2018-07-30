package togit.migration.sources.ccbase.extractions

import togit.migration.plan.Extraction
import togit.migration.plan.Snapshot

class LabelExtraction extends Extraction {

    String key

    LabelExtraction(String key) {
        this.key = key
    }

    @Override Map<String, Object> extract(Snapshot snapshot) {
        [:].put(key, snapshot.identifier)
    }
}
