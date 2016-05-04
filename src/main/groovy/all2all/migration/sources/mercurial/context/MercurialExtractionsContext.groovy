package all2all.migration.sources.mercurial.context

import all2all.context.base.Context
import all2all.migration.sources.mercurial.extractions.ChangesetProperties

trait MercurialExtractionsContext implements Context {

    void ChangesetId(Map<String, String> map) {
        //FIXME: ChangesetProperties' constructor takes an argument
        extractions.add(new ChangesetProperties())
    }

}
