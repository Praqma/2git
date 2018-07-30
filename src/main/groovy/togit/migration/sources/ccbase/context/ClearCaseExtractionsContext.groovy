package togit.migration.sources.ccbase.context

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import togit.context.Context
import togit.migration.sources.ccbase.extractions.LabelExtraction

class ClearCaseExtractionsContext implements Context {
    final static Logger LOG = LoggerFactory.getLogger(this.class)

    /**
     * Maps the Label to the given key
     * @param key the key to map the label to
     */
    void label(String key) {
        extractions.add(new LabelExtraction(key))
        LOG.debug("Added 'label' extraction")
    }
}
