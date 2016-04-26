package all2all.migration.sources.ccucm.context

import all2all.context.base.Context
import all2all.migration.sources.ccucm.extractions.BaselineProperty
import org.slf4j.Logger
import org.slf4j.LoggerFactory

//@Slf4j
trait CcucmExtractionsContext implements Context {
    final static Logger log = LoggerFactory.getLogger(CcucmExtractionsContext.class)

    /**
     * Extracts a CoolBaseline property
     * @param map A map of values to extract and keys to map them to.
     */
    void baselineProperty(Map<String, String> map) {
        extractions.add(new BaselineProperty(map))
        log.info("Added 'baselineProperty' criteria.")
    }
}
