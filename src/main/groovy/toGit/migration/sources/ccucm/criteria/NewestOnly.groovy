package toGit.migration.sources.ccucm.criteria

import net.praqma.clearcase.ucm.utils.BaselineList
import org.slf4j.LoggerFactory
import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot
import toGit.migration.sources.ccucm.Baseline

class NewestOnly extends Criteria {

    final static log = LoggerFactory.getLogger(this.class)

    @Override
    boolean appliesTo(Snapshot snapshot, List<Snapshot> allSnapshots) {
        return allSnapshots == null ? true : snapshot == allSnapshots[-1]
    }
}
