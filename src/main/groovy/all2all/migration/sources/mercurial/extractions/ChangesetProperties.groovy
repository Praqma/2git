package all2all.migration.sources.mercurial.extractions

import all2all.migration.plan.Snapshot

class ChangesetProperties {

    String user, message
    Date date

    ChangesetProperties(Snapshot snapshot) {

    }
}
