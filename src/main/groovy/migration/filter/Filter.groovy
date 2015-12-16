package migration.filter

@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
import migration.filter.actions.Action
import migration.filter.criterias.Criteria
import migration.filter.extractions.Extraction

@Slf4j
class Filter {
    List<Criteria> criteria = []
    List<Extraction> extractions = []
    List<Action> actions = []
    List<Filter> filters = []
}
