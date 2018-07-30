package toGit.migration.sources.ccucm.context

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import toGit.context.Context
import toGit.migration.sources.ccucm.extractions.BaselineProperty

trait CcucmExtractionsContext implements Context {
    final static Logger log = LoggerFactory.getLogger(this.class)

    /**
     * Extracts a CoolBaseline property
     * @param map A map of values to extract and keys to map them to.
     */
    void baselineProperty(Map<String, String> map) {
        extractions.add(new BaselineProperty(map))
        log.debug("Added 'baselineProperty' extraction.")
    }
}
