package clearCase

import migration.Criteria
import net.praqma.clearcase.ucm.utils.BaselineFilter
import net.praqma.clearcase.ucm.utils.BaselineList

/**
 * BaselineFilter that aggregates passed in Criteria
 */
class AggregatedBaselineFilter extends BaselineFilter {

    List<Criteria> criteria

    def AggregatedBaselineFilter(List<Criteria> criteria) {
        this.criteria = criteria
    }

    @Override
    int filter(BaselineList baselines) {
        int removed = 0
        def baselineIterator = baselines.iterator()
        while (baselineIterator.hasNext()) {
            def baseline = baselineIterator.next()
            for (Criteria crit : criteria) {
                if (!crit.appliesTo(baseline)) {
                    baselineIterator.remove()
                    removed++
                    break
                }
            }
        }
        return removed
    }

    @Override
    String getName() {
        return "AggregatedBaselineFilter"
    }
}
