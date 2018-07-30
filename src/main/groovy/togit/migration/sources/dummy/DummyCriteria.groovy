package togit.migration.sources.dummy

import togit.migration.plan.Criteria
import togit.migration.plan.Snapshot

class DummyCriteria extends Criteria {

    @Override
    boolean appliesTo(Snapshot snapshot, List<Snapshot> allSnapshots) {
        true
    }
}
