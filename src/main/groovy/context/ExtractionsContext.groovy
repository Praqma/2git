package context

import context.base.Context
import context.traits.HasExtractions
import migration.plan.Extraction
import migration.sources.Snapshot

class ExtractionsContext implements Context, HasExtractions {

    /**
     * Runs a custom closure to map values
     * @param closure Closure to run
     */
    void custom(Closure<HashMap<String, Object>> closure) {
        extractions.add(new Extraction() {
            @Override
            HashMap<String, Object> extract(Snapshot snapshot) {
                closure.delegate = this
                closure.resolveStrategy = Closure.DELEGATE_FIRST
                return closure.call(snapshot)
            }
        })
        log.info("Added 'custom' extraction.")
    }
}