package togit.migration.sources.ccucm.criteria

import org.slf4j.LoggerFactory
import togit.migration.plan.Criteria
import togit.migration.plan.Snapshot
import togit.migration.sources.ccucm.Baseline

class PromotionLevels extends Criteria {

    final static LOG = LoggerFactory.getLogger(this.class)

    String[] levels

    PromotionLevels(String... levels) {
        this.levels = levels
    }

    @Override
    boolean appliesTo(Snapshot snapshot, List<Snapshot> allSnapshots) {
        Baseline baseline = ((Baseline) snapshot).source
        LOG.debug("Testing '${baseline.shortname} (${baseline.promotionLevel})' against promotionLevels '$levels'")
        boolean result = levels.contains(baseline.promotionLevel.toString())
        LOG.debug("Result: ${result ? 'MATCH' : 'no match'}")
        result
    }
}
