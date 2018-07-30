package togit.migration.sources.ccucm.criteria

import org.slf4j.LoggerFactory
import togit.migration.plan.Criteria
import togit.migration.plan.Snapshot

class NewestOnly extends Criteria {

    final static LOG = LoggerFactory.getLogger(this.class)

    @Override
    boolean appliesTo(Snapshot snapshot, List<Snapshot> allSnapshots) {
        allSnapshots == null ? true : snapshot == allSnapshots[-1]
    }
}
