package toGit.migration.sources.ccucm.extractions

import toGit.migration.plan.Extraction
import toGit.migration.plan.Snapshot
import toGit.migration.sources.ccucm.Baseline

class BaselineProperty extends Extraction {
    Map<String, String> map

    BaselineProperty(Map<String, String> map) {
        this.map = map
    }

    @Override
    HashMap<String, Object> extract(Snapshot snapshot) {
        def result = [:]
        map.each { mv ->
            println "Extracting baseline property '$mv.value'"
            result.put(mv.key, ((Baseline) snapshot).source."$mv.value")
        }
        return result
    }
}