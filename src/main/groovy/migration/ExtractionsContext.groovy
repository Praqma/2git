package migration

@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
import net.praqma.clearcase.ucm.entities.Baseline as CoolBaseline

@Slf4j
class ExtractionsContext {
    List<Extraction> extractions = []

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
}
