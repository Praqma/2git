package toGit.migration.sources.ccucm.criteria

import org.slf4j.LoggerFactory
import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot

class BaselineNames extends Criteria {

    final static log = LoggerFactory.getLogger(this.class)

    List<String> baselines

    BaselineNames(String... baselines) {
        this.baselines = baselines
    }

    BaselineNames(List<String> baselines) {
        this.baselines = baselines
    }

    @Override
    boolean appliesTo(Snapshot snapshot, List<Snapshot> allSnapshots) {
        log.debug("Testing '$snapshot' against baseline list $baselines")
        def result = baselines.contains(snapshot.toString())
        log.debug("Result: " + (result ? "MATCH" : "no match"))
        return result
    }
}