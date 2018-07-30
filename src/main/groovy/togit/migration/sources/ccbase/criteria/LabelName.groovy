package togit.migration.sources.ccbase.criteria

import togit.migration.plan.Criteria
import togit.migration.plan.Snapshot

class LabelName extends Criteria {

    String regex

    LabelName(String regex) {
        this.regex = regex
    }

    @Override
    boolean appliesTo(Snapshot snapshot, List<Snapshot> allSnapshots) {
        snapshot.identifier =~ regex
    }
}
