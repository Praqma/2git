package togit.migration.sources.ccucm

import net.praqma.clearcase.ucm.utils.BaselineFilter
import net.praqma.clearcase.ucm.utils.BaselineList
import togit.migration.plan.Criteria

/**
 * BaselineFilter that aggregates passed in Criteria
 */
class AggregatedBaselineFilter extends BaselineFilter {

    final String name = 'AggregatedBaselineFilter'

    List<Criteria> criteria

    /**
     * AggregatedBaselineFilter constructor
     * @param criteria criteria to plan BaselineLists with
     */
    AggregatedBaselineFilter(List<Criteria> criteria) {
        this.criteria = criteria
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int filter(BaselineList baselines) {
        int removed = 0
        Iterator baselineIterator = baselines.iterator()
        // Make a copy of the set of baselines we've discovered.
        // Purpose: Pass it in so we can select only the last element in a filter
        List clonedBaselines = baselines.clone().collect { new Baseline(it) }
        while (baselineIterator.hasNext()) {
            Baseline snapshot = new Baseline(baselineIterator.next())
            if (!snapshot.matches(criteria, clonedBaselines)) {
                baselineIterator.remove()
                removed++
            }
        }
        removed
    }
}
