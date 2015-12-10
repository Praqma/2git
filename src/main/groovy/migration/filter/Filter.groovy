package migration.filter

import clearcase.AggregatedBaselineFilter
import clearcase.Cool
@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
import migration.filter.actions.Action
import migration.filter.criterias.Criteria
import migration.filter.extractions.Extraction
import net.praqma.clearcase.ucm.utils.BaselineFilter
import net.praqma.clearcase.ucm.utils.BaselineList
import net.praqma.clearcase.ucm.entities.Component as CoolComponent
import net.praqma.clearcase.ucm.entities.Stream as CoolStream

@Slf4j
class Filter {
    List<Criteria> criteria = []
    List<Extraction> extractions = []
    List<Action> actions = []

    /**
     * Filters given or retrieves new baselines using configured criteria
     * @param baselines the baselines to filter
     * @param sourceComponent the component to get the baselines for
     * @param sourceStream the stream to get the baselines for
     * @return the filtered BaselineList
     */
    BaselineList getBaselines(BaselineList baselines, CoolComponent sourceComponent, CoolStream sourceStream) {
        log.debug('Entering getBaselines().')
        BaselineFilter baselineFilter = new AggregatedBaselineFilter(criteria)
        baselines = baselines ? baselines.applyFilter(baselineFilter) : Cool.getBaselines(sourceComponent, sourceStream, baselineFilter)
        log.info("Found {} baselines matching given requirements: {}", baselines.size(), baselines)
        log.debug('Exiting getBaselines().')
        return baselines
    }
}
