package togit.migration.sources.dummy

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import togit.context.Context

class DummyCriteriaContext implements Context {
    final static Logger LOG = LoggerFactory.getLogger(this.class)

    void dummyCriteria() {
        criteria.add(new DummyCriteria())
        LOG.debug("Added 'dummyCriteria' criteria")
    }
}
