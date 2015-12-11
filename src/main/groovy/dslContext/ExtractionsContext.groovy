package dslContext

@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
import migration.filter.extractions.Extraction
import net.praqma.clearcase.ucm.entities.Baseline
import net.praqma.clearcase.ucm.entities.Baseline as CoolBaseline

@Slf4j
class ExtractionsContext {
    List<Extraction> extractions = []

    /**
     * Extracts a CoolBaseline property
     * @param mappingValues A map of values to extract and keys to map them to.
     */
    def void baselineProperty(Map<String, String> mappingValues) {
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
    }

    /**
     * Runs a custom closure to extract values.
     * @param cl Closure to run, returns HashMap<String, Object>, runs in the Baseline's context
     */
    def void custom(Closure<HashMap<String, Object>> cl) {
        extractions.add(new Extraction() {
            @Override
            HashMap<String, Object> extract(Baseline baseline) {
                cl.resolveStrategy = Closure.DELEGATE_ONLY
                cl.delegate = baseline
                return cl.call()
            }
        })
    }
}