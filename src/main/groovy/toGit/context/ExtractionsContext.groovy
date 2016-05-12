package toGit.context

import toGit.context.base.Context
import toGit.context.traits.HasExtractions
import toGit.migration.plan.Extraction
import toGit.migration.plan.Snapshot
import groovy.util.logging.Slf4j

@Slf4j
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