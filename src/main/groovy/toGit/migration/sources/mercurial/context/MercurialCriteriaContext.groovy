package toGit.migration.sources.mercurial.context

import toGit.context.base.Context
import toGit.migration.sources.mercurial.criteria.MercurialAfterDate

trait MercurialCriteriaContext implements Context {

    void afterDate(String format, String date) {
        criteria.add(new MercurialAfterDate(format, date))
    }

    void afterCommit() {}

    void afterTag() {}
}