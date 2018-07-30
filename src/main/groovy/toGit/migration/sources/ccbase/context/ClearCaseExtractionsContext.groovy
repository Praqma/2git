package toGit.migration.sources.ccbase.context

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import toGit.context.Context
import toGit.migration.sources.ccbase.extractions.LabelExtraction

trait ClearCaseExtractionsContext implements Context {
    final static Logger log = LoggerFactory.getLogger(this.class)

    /**
     * Maps the Label to the given key
     * @param key the key to map the label to
     */
    void label(String key) {
        extractions.add(new LabelExtraction(key))
        log.debug("Added 'label' extraction")
    }
}
