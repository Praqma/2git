package toGit.migration.sources.ccucm.criteria

import org.slf4j.LoggerFactory
import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot
import toGit.migration.sources.ccucm.Baseline

class AfterDate extends Criteria {

    final static log = LoggerFactory.getLogger(this.class)

    Date date

    AfterDate(Date date) {
        this.date = date
    }

    AfterDate(String format, String date) {
        this(Date.parse(format, date))
    }

    @Override
    boolean appliesTo(Snapshot snapshot) {
        def baseline = ((Baseline) snapshot).source
        log.debug("Testing '${baseline.shortname} (${baseline.date})' against date '${date}'")
        def result = baseline.date > date
        log.debug("Result: " + (result ? "MATCH" : "no match"))
        return result
    }
}
