package toGit.migration.sources.ccucm.criteria

import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot
import toGit.migration.sources.ccucm.Baseline

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