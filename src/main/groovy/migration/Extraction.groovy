package migration

@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
import net.praqma.clearcase.ucm.entities.Baseline as CoolBaseline

@Slf4j
abstract class Extraction {
    def abstract HashMap<String, Object> extract(CoolBaseline baseline)
}
