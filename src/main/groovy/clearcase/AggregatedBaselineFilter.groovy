package clearcase

import migration.filter.criterias.Criteria
import net.praqma.clearcase.ucm.utils.BaselineFilter
import net.praqma.clearcase.ucm.utils.BaselineList

/**
 * BaselineFilter that aggregates passed in Criteria
 */
class AggregatedBaselineFilter extends BaselineFilter {

    List<Criteria> criteria // criteria to filter BaselineLists with

    /**
     * AggregatedBaselineFilter constructor
     * @param criteria criteria to filter BaselineLists with
     */
    public AggregatedBaselineFilter(List<Criteria> criteria) {
        this.criteria = criteria
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    String getName() {
        return "AggregatedBaselineFilter"
    }
}
