package toGit.migration.sources.ccbase.context

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import toGit.context.base.Context
import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot
import toGit.migration.sources.ccbase.criteria.LabelCriteria

trait ClearcaseCriteriaContext implements Context {
    final static Logger log = LoggerFactory.getLogger(ClearcaseCriteriaContext.class)

    void afterLabel(String label) {
        criteria.add(new LabelCriteria(label))
    }
}
