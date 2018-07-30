package togit.migration.sources.ccucm.context

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import togit.context.Context
import togit.migration.sources.ccucm.extractions.BaselineProperty

class CcucmExtractionsContext implements Context {
    final static Logger LOG = LoggerFactory.getLogger(this.class)

    /**
     * Extracts a CoolBaseline property
     * @param map A map of values to extract and keys to map them to.
     */
    void baselineProperty(Map<String, String> map) {
        extractions.add(new BaselineProperty(map))
        LOG.debug("Added 'baselineProperty' extraction.")
    }
}
