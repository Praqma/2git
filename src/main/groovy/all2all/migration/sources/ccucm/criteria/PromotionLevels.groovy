package all2all.migration.sources.ccucm.criteria

import all2all.migration.plan.Criteria
import all2all.migration.plan.Snapshot
import all2all.migration.sources.ccucm.Baseline

class PromotionLevels extends Criteria {
    String[] levels

    PromotionLevels(String... levels) {
        this.levels = levels
    }

    @Override
    boolean appliesTo(Snapshot snapshot) {
        def baseline = ((Baseline) snapshot).source
        println "Testing '" + baseline.shortname + " (" + baseline.promotionLevel + ")' against promotionLevels '" + promotionLevels + "'."
        def result = levels.contains(baseline.promotionLevel.toString())
        println "Result: " + (result ? "SUCCESS" : "FAILURE")
        return result
    }
}