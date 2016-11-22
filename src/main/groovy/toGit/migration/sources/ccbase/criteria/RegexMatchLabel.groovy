package toGit.migration.sources.ccbase.criteria

import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot

class RegexMatchLabel extends Criteria{

    String regex

    RegexMatchLabel(String regex) {
        this.regex = regex;
    }

    @Override
    boolean appliesTo(Snapshot snapshot) {
        return snapshot.identifier =~ regex
    }
}
