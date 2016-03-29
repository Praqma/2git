package dslContext

import dslContext.base.Context
import groovy.util.logging.Slf4j
import migration.filter.extractions.Extraction
import net.praqma.clearcase.ucm.entities.Baseline
import net.praqma.clearcase.ucm.entities.Baseline as CoolBaseline

@Slf4j
class ExtractionsContext implements Context {
    List<Extraction> extractions = []

    /**
     * Extracts a CoolBaseline property
     * @param mappingValues A map of values to extract and keys to map them to.
     */
    def void baselineProperty(Map<String, String> mappingValues) {
        log.debug("Entering baselineProperty().")
        extractions.add(new Extraction() {
            @Override
            HashMap<String, Object> extract(CoolBaseline baseline) {
                def map = [:]
                mappingValues.each { mv ->
                    map.put(mv.key, baseline.properties."$mv.value")
                }
                return map
            }
        })
        log.debug("Exiting baselineProperty().")
    }

    /**
     * Runs a custom closure to extract values.
     * @param closure Closure to run, returns HashMap<String, Object>, passes in the Baseline
     */
    def void custom(Closure<HashMap<String, Object>> closure) {
        extractions.add(new Extraction() {
            @Override
            HashMap<String, Object> extract(Baseline baseline) {
                closure.delegate = this
                closure.resolveStrategy = Closure.DELEGATE_FIRST
                return closure.call(baseline)
            }
        })
    }
}