package toGit.migration.sources.ccucm.context

import toGit.context.base.Context
import toGit.migration.sources.ccucm.extractions.BaselineProperty
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
