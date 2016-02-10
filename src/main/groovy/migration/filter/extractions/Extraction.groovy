package migration.filter.extractions

import groovy.util.logging.Slf4j
import net.praqma.clearcase.ucm.entities.Baseline as CoolBaseline

@Slf4j
abstract class Extraction {
    def abstract HashMap<String, Object> extract(CoolBaseline baseline)
}
