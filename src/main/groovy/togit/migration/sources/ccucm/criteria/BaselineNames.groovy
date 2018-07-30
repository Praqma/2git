package togit.migration.sources.ccucm.criteria

import org.slf4j.LoggerFactory
import togit.migration.plan.Criteria
import togit.migration.plan.Snapshot

class BaselineNames extends Criteria {

    final static LOG = LoggerFactory.getLogger(this.class)

    List<String> baselines

    BaselineNames(String... baselines) {
        thislines = baselines
    }

    BaselineNames(List<String> baselines) {
        thislines = baselines
    }

    @Override
    boolean appliesTo(Snapshot snapshot, List<Snapshot> allSnapshots) {
        LOG.debug("Testing '$snapshot' against baseline list $baselines")
        boolean result = baselines.contains(snapshot.toString())
        LOG.debug("Result: ${result ? 'MATCH' : 'no match'}")
        result
    }
}
