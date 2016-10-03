package toGit.context

import org.slf4j.LoggerFactory
import toGit.context.base.Context
import toGit.context.traits.HasExtractions
import toGit.migration.plan.Extraction
import toGit.migration.plan.Snapshot

class ExtractionsContext implements Context, HasExtractions {

    final static log = LoggerFactory.getLogger(this.class)

    /**
     * Runs a custom closure to map values
     * @param closure Closure to run
     */
    void custom(Closure<HashMap<String, Object>> closure) {
        log.debug("Registering extraction - custom")
        extractions.add(new Extraction() {
            @Override
            HashMap<String, Object> extract(Snapshot snapshot) {
                closure.delegate = this
                closure.resolveStrategy = Closure.DELEGATE_FIRST
                return closure.call(snapshot)
            }
        })
        log.debug("Registered extraction - custom")
    }
}