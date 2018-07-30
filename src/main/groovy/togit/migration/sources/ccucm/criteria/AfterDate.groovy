package togit.migration.sources.ccucm.criteria

import org.slf4j.LoggerFactory
import togit.migration.plan.Criteria
import togit.migration.plan.Snapshot
import togit.migration.sources.ccucm.Baseline

class AfterDate extends Criteria {

    final static LOG = LoggerFactory.getLogger(this.class)

    Date date

    AfterDate(Date date) {
        this.date = date
    }

    AfterDate(String format, String date) {
        this(Date.parse(format, date))
    }

    @Override
    boolean appliesTo(Snapshot snapshot, List<Snapshot> allSnapshots) {
        Baseline baseline = ((Baseline) snapshot).source
        LOG.debug("Testing '${baseline.shortname} (${baseline.date})' against date '${date}'")
        boolean result = baseline.date > date
        LOG.debug("Result: ${result ? 'MATCH' : 'no match'}")
        result
    }
}
