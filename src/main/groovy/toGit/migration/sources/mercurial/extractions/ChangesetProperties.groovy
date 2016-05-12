package toGit.migration.sources.mercurial.extractions

import toGit.migration.plan.Snapshot

class ChangesetProperties {

    String user, message
    Date date

    ChangesetProperties(Snapshot snapshot) {

    }
}
