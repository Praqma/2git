package toGit.migration.sources.ccm.context

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import toGit.context.base.Context

import toGit.migration.sources.ccm.criteria.AlreadyConverted

trait CcmCriteriaContext implements Context {
    final static Logger log = LoggerFactory.getLogger(this.class)


    /**
     * Filters out baselines that don't have given promotion levels
     * @param levels the promotion levels to test baselines against
     */
    void AlreadyConverted() {
        criteria.add(new AlreadyConverted())
        log.debug("Added 'AlreadyConverted' criteria.")
    }
}
