package all2all.migration.sources.mercurial

import all2all.migration.plan.Snapshot

class MercurialChangeset extends Snapshot {

    Date date

    MercurialChangeset(String identifier) {
        super(identifier)
    }
}
