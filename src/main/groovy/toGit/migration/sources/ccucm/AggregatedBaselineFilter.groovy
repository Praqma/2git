package toGit.migration.sources.ccucm

import net.praqma.clearcase.ucm.utils.BaselineFilter
import net.praqma.clearcase.ucm.utils.BaselineList
import toGit.migration.plan.Criteria

/**
 * BaselineFilter that aggregates passed in Criteria
 */
class AggregatedBaselineFilter extends BaselineFilter {

    List<Criteria> criteria // criteria to plan BaselineLists with

    /**
     * AggregatedBaselineFilter constructor
     * @param criteria criteria to plan BaselineLists with
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
            def snapshot = new Baseline(baselineIterator.next())
            if (!snapshot.matches(criteria, baselineList.collect { new Baseline(it) })) {
                baselineIterator.remove()
                removed++
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
