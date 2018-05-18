package toGit.migration.sources.ccucm.criteria

import org.slf4j.LoggerFactory
import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot
import toGit.migration.sources.ccucm.Baseline

class PromotionLevels extends Criteria {

    final static log = LoggerFactory.getLogger(this.class)

    String[] levels

    PromotionLevels(String... levels) {
        this.levels = levels
    }

    @Override
    boolean appliesTo(Snapshot snapshot, List<Snapshot> allSnapshots) {
        def baseline = ((Baseline) snapshot).source
        log.debug("Testing '${baseline.shortname} (${baseline.promotionLevel})' against promotionLevels '$levels'")
        def result = levels.contains(baseline.promotionLevel.toString())
        log.debug("Result: " + (result ? "MATCH" : "no match"))
        return result
    }
}