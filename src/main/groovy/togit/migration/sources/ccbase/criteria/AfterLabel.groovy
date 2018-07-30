package togit.migration.sources.ccbase.criteria

import togit.migration.plan.Criteria
import togit.migration.plan.Snapshot

class AfterLabel extends Criteria {

    String label

    AfterLabel(String label) {
        this.label = label
    }

    @Override
    boolean appliesTo(Snapshot snapshot, List<Snapshot> allSnapshots) {
        label < snapshot.identifier
    }
}
