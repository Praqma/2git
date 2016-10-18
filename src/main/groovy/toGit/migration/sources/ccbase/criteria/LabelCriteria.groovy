package toGit.migration.sources.ccbase.criteria

import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot

class LabelCriteria extends Criteria{

    String label

    LabelCriteria(String label) {
        this.label = label;
    }

    @Override
    boolean appliesTo(Snapshot snapshot) {
        return label.compareTo(snapshot.identifier) < 0
    }
}
