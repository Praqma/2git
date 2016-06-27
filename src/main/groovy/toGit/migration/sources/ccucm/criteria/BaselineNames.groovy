package toGit.migration.sources.ccucm.criteria

import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot
import toGit.migration.sources.ccucm.Baseline

class BaselineNames extends Criteria {
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
        println "Testing '" + baseline.shortname + "' against baseline list $baselines."
        def result = baselines.contains(baseline.shortname)
        println "Result: " + (result ? "MATCH" : "no match")
        return result
    }
}