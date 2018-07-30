package togit.migration.sources.ccucm.extractions

import org.slf4j.LoggerFactory
import togit.migration.plan.Extraction
import togit.migration.plan.Snapshot
import togit.migration.sources.ccucm.Baseline

class BaselineProperty extends Extraction {

    final static LOG = LoggerFactory.getLogger(this.class)

    Map<String, String> map

    BaselineProperty(Map<String, String> map) {
        this.map = map
    }

    @Override
    Map<String, Object> extract(Snapshot snapshot) {
        Map result = [:]
        map.each { propertyName ->
            LOG.debug("Extracting baseline property '${propertyName.value}'")
            Object value = ((Baseline) snapshot).source."$propertyName.value"
            LOG.debug("Extracted value ${value}")
            result.put(propertyName.key, value)
        }
        result
    }
}
