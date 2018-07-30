package togit.migration.sources.ccbase.context

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import togit.context.Context
import togit.migration.sources.ccbase.criteria.AfterLabel
import togit.migration.sources.ccbase.criteria.LabelName

class ClearCaseCriteriaContext implements Context {
    final static Logger LOG = LoggerFactory.getLogger(this.class)

    /**
     * Selects labels that occur after the given label
     * Note: Assumes alphabetical sorting == date sorting
     * @param label The starting point label
     */
    void afterLabel(String label) {
        criteria.add(new AfterLabel(label))
        LOG.debug("Added 'afterLabel' criteria")
    }

    /**
     * Selects labels that don't math the given regex
     * @param regex The regex to match with
     */
    void labelName(String regex) {
        criteria.add(new LabelName(regex))
        LOG.debug("Added 'labelName' criteria")
    }
}
