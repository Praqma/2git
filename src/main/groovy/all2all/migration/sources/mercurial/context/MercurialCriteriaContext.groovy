package all2all.migration.sources.mercurial.context

import all2all.context.base.Context
import all2all.migration.sources.mercurial.criteria.MercurialAfterDate

trait MercurialCriteriaContext implements Context {

    void afterDate(String format, String date) {
        criteria.add(new MercurialAfterDate(format, date))
    }

    void afterCommit() {}

    void afterTag() {}
}