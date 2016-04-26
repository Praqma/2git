package all2all.migration.sources.ccucm.criteria

import all2all.migration.plan.Criteria
import all2all.migration.plan.Snapshot
import all2all.migration.sources.ccucm.Baseline

class BaselineName extends Criteria {
    String regex

    BaselineName(String regex) {
        this.regex = regex
    }

    @Override
    boolean appliesTo(Snapshot snapshot) {
        def baseline = ((Baseline) snapshot).source
        println "Testing '" + baseline.shortname + "' against regex '" + regex + "'."
        def matcher = baseline.shortname =~ regex
        def result = matcher.matches()
        println "Result: " + (result ? "SUCCESS" : "FAILURE")
        return result
    }
}