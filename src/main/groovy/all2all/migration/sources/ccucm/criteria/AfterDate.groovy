package all2all.migration.sources.ccucm.criteria

import all2all.migration.plan.Criteria
import all2all.migration.plan.Snapshot
import all2all.migration.sources.ccucm.Baseline

class AfterDate extends Criteria {
    Date date

    AfterDate(String format, String date) {
        this.date = Date.parse(format, date)
    }

    AfterDate(Date date) {
        this.date = date
    }

    @Override
    boolean appliesTo(Snapshot snapshot) {
        def baseline = ((Baseline) snapshot).source
        println "Testing '" + baseline.shortname + " (" + baseline.date + ")' against date '" + date + "'."
        def result = baseline.date > date
        println "Result: " + (result ? "SUCCESS" : "FAILURE")
        return result
    }
}
