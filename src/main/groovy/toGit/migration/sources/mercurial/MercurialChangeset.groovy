package toGit.migration.sources.mercurial

import toGit.migration.plan.Snapshot

class MercurialChangeset extends Snapshot {

    Date date

    MercurialChangeset(String identifier) {
        super(identifier)
    }
}
