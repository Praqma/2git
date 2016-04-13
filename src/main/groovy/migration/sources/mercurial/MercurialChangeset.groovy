package migration.sources.mercurial

import migration.sources.Snapshot

class MercurialChangeSet extends Snapshot {

    Date date

    MercurialChangeSet(String identifier) {
        super(identifier)
    }

}
