package togit.migration.sources.dummy

import org.slf4j.LoggerFactory
import togit.context.Context

class DummyExtractionsContext implements Context {

    final static LOG = LoggerFactory.getLogger(this.class)

    void dummyExtraction() {
        LOG.debug('Registering extraction - dummyExtraction')
        extractions.add(null)
        LOG.debug('Regisered extraction - dummyExtraction')
    }
}
