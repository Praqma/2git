package all2all.migration.sources.mercurial.extractions

import all2all.migration.plan.Snapshot

/**
 * Created by Timea Kiss on 06/04/16.
 */
class ChangeSetProperties {

    String user, message
    Date date

    ChangeSetProperties(Snapshot snapshot) {

    }
}
