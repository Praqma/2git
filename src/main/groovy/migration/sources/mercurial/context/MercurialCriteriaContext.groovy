package migration.sources.mercurial.context

import context.base.Context
import context.traits.HasCriteria
import migration.plan.Criteria
import migration.sources.mercurial.criteria.MercurialAfterDate

trait MercurialCriteriaContext implements Context, HasCriteria{

    void afterDate(String format, String date) {
        criteria.add(new MercurialAfterDate(format, date))
    }

    void afterCommit() {}

    void afterTag(){}
}