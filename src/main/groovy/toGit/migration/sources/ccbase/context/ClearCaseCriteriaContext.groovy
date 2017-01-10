package toGit.migration.sources.ccbase.context

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import toGit.context.base.Context
import toGit.migration.sources.ccbase.criteria.AfterLabel
import toGit.migration.sources.ccbase.criteria.LabelName

trait ClearCaseCriteriaContext implements Context {
    final static Logger log = LoggerFactory.getLogger(this.class)

    /**
     * Selects labels that occur after the given label
     * Note: Assumes alphabetical sorting == date sorting
     * @param label The starting point label
     */
    void afterLabel(String label) {
        criteria.add(new AfterLabel(label))
        log.debug("Added 'afterLabel' criteria")
    }

    /**
     * Selects labels that don't math the given regex
     * @param regex The regex to match with
     */
    void labelName(String regex) {
        criteria.add(new LabelName(regex))
        log.debug("Added 'labelName' criteria")
    }
}
