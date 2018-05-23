package toGit.migration.sources.ccucm.criteria

import org.slf4j.LoggerFactory
import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot

class NewestOnly extends Criteria {

    final static log = LoggerFactory.getLogger(this.class)

    @Override
    boolean appliesTo(Snapshot snapshot, List<Snapshot> allSnapshots) {
        return allSnapshots == null ? true : snapshot == allSnapshots[-1]
    }
}
