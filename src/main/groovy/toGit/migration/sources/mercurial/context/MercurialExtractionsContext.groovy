package toGit.migration.sources.mercurial.context

import toGit.context.base.Context
import toGit.migration.sources.mercurial.extractions.ChangesetProperties

trait MercurialExtractionsContext implements Context {

    void ChangesetId(Map<String, String> map) {
        //FIXME: ChangesetProperties' constructor takes an argument
        extractions.add(new ChangesetProperties())
    }

}
