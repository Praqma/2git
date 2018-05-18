package toGit.migration.sources.ccbase.criteria

import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot

class LabelName extends Criteria {

    String regex

    LabelName(String regex) {
        this.regex = regex;
    }

    @Override
    boolean appliesTo(Snapshot snapshot, List<Snapshot> allSnapshots) {
        return snapshot.identifier =~ regex
    }
}
