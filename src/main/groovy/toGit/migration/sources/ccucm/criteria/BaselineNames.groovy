package toGit.migration.sources.ccucm.criteria

import org.slf4j.LoggerFactory
import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot
import toGit.migration.sources.ccucm.Baseline

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
    boolean appliesTo(Snapshot snapshot) {
        def baseline = ((Baseline) snapshot).source
        // String.equals(GString) fails, hence the extra toString
        def baselineName = "${baseline.shortname}@${baseline.PVob.name}".toString()
        log.debug("Testing '$baselineName' against baseline list $baselines")
        def result = baselines.contains(baselineName)
        log.debug("Result: " + (result ? "MATCH" : "no match"))
        return result
    }
}