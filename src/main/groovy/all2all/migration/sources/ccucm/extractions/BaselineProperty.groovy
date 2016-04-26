package all2all.migration.sources.ccucm.extractions

import all2all.migration.plan.Extraction
import all2all.migration.plan.Snapshot
import all2all.migration.sources.ccucm.Baseline

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