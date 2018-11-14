package togit.context

import org.slf4j.LoggerFactory
import togit.migration.plan.Extraction
import togit.migration.plan.Snapshot

class ExtractionsContext implements Context {

    final static LOG = LoggerFactory.getLogger(this.class)

    final List<Extraction> extractions = []

    /**
     * Runs a custom closure to map values
     * @param closure Closure to run
     */
    void custom(Closure<HashMap<String, Object>> closure) {
        LOG.debug('Registering extraction - custom')
        extractions.add(new Extraction() {
            @Override
            HashMap<String, Object> extract(Snapshot snapshot) {
                closure.delegate = this
                closure.resolveStrategy = Closure.DELEGATE_FIRST
                closure.call(snapshot)
            }
        })
        LOG.debug('Registered extraction - custom')
    }
}
