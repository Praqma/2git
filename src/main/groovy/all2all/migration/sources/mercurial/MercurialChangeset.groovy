package all2all.migration.sources.mercurial

import all2all.migration.plan.Snapshot

class MercurialChangeSet extends Snapshot {

    Date date

    MercurialChangeSet(String identifier) {
        super(identifier)
    }

}
