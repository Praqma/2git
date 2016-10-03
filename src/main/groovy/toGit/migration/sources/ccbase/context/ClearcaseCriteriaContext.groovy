package toGit.migration.sources.ccbase.context

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import toGit.context.base.Context
import toGit.migration.sources.ccbase.criteria.AfterLabel

trait ClearcaseCriteriaContext implements Context {
    final static Logger log = LoggerFactory.getLogger(ClearcaseCriteriaContext.class)

    void afterLabel(String label) {
        criteria.add(new AfterLabel(label))
    }
}
