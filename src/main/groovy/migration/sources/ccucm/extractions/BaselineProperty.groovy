package migration.sources.ccucm.extractions

import migration.plan.Extraction
import migration.sources.Snapshot
import migration.sources.ccucm.Baseline

class BaselineProperty extends Extraction {
    Map<String, String> map

    BaselineProperty(Map<String, String> map) {
        this.map = map
    }

    @Override
    HashMap<String, Object> extract(Snapshot snapshot) {
        def result = [:]
        map.each { mv ->
            result.put(mv.key, ((Baseline) snapshot).source.properties."$mv.value")
        }
        return result
    }
}