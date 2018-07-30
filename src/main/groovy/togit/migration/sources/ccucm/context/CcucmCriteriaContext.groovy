package togit.migration.sources.ccucm.context

import net.praqma.clearcase.ucm.entities.Baseline as CoolBaseline
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import togit.context.Context
import togit.migration.sources.ccucm.criteria.AfterDate
import togit.migration.sources.ccucm.criteria.BaselineName
import togit.migration.sources.ccucm.criteria.BaselineNames
import togit.migration.sources.ccucm.criteria.NewestOnly
import togit.migration.sources.ccucm.criteria.PromotionLevels

class CcucmCriteriaContext implements Context {
    final static Logger LOG = LoggerFactory.getLogger(this.class)

    /**
     * Only include the newest baseline in stream
     */
    void newestOnly() {
        criteria.add(new NewestOnly())
        LOG.debug("Added 'newestOnly' criteria")
    }

    /**
     * Filters out baselines that were created before a baseline
     * @param name the name of the baseline
     */
    void afterBaseline(String name) {
        criteria.add(new AfterDate(CoolBaseline.get(name).date))
        LOG.debug("Added 'afterBaseline' criteria.")
    }

    /**
     * Filters out baselines that were created before a date
     * @param format the date format
     * @param date the date
     */
    void afterDate(String format, String date) {
        criteria.add(new AfterDate(format, date))
        LOG.debug("Added 'afterDate' criteria.")
    }

    /**
     * Filters out baselines that were created before a date
     * @param date the date
     */
    void afterDate(Date date) {
        criteria.add(new AfterDate(date))
        LOG.debug("Added 'afterDate' criteria.")
    }

    /**
     * Filters out baselines whose name don't match the regex
     * @param regex the regex to test baseline names against
     */
    void baselineName(String regex) {
        criteria.add(new BaselineName(regex))
        LOG.debug("Added 'baselineName' criteria.")
    }

    /**
     * Filters out baselines whose names aren't specified
     * @param names The baseline names to select
     */
    void baselineNames(String... names) {
        criteria.add(new BaselineNames(names))
        LOG.debug("Added 'baselineNames' criteria.")
    }

    /**
     * Filters out baselines whose names aren't specified
     * @param names The baseline names to select
     */
    void baselineNames(List<String> names) {
        criteria.add(new BaselineNames(names))
        LOG.debug("Added 'baselineNames' criteria.")
    }

    /**
     * Filters out baselines that don't have given promotion levels
     * @param levels the promotion levels to test baselines against
     */
    void promotionLevels(String... levels) {
        criteria.add(new PromotionLevels(levels))
        LOG.debug("Added 'promotionLevels' criteria.")
    }
}
