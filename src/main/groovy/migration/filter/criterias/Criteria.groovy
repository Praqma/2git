package migration.filter.criterias

import groovy.util.logging.Slf4j
import net.praqma.clearcase.ucm.entities.Baseline as CoolBaseline

@Slf4j
abstract class Criteria {
    def abstract boolean appliesTo(CoolBaseline baseline)
}
