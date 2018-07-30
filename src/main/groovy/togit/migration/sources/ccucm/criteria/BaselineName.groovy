package togit.migration.sources.ccucm.criteria

import org.slf4j.LoggerFactory
import togit.migration.plan.Criteria
import togit.migration.plan.Snapshot
import togit.migration.sources.ccucm.Baseline

class BaselineName extends Criteria {

    final static LOG = LoggerFactory.getLogger(this.class)

    String regex

    BaselineName(String regex) {
        this.regex = regex
    }

    @Override
    boolean appliesTo(Snapshot snapshot, List<Snapshot> allSnapshots) {
        Baseline baseline = ((Baseline) snapshot).source
        LOG.debug("Testing '${baseline.shortname}' against regex '${regex}'")
        boolean result = (baseline.shortname =~ regex).matches()
        LOG.debug("Result: ${result ? 'MATCH' : 'no match'}")
        result
    }
}
