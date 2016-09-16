package toGit.migration.sources.ccucm.context

import toGit.context.base.Context
import toGit.migration.sources.ccucm.criteria.*
import net.praqma.clearcase.ucm.entities.Baseline as CoolBaseline
import org.slf4j.Logger
import org.slf4j.LoggerFactory

trait CcucmCriteriaContext implements Context {
    final static Logger log = LoggerFactory.getLogger(CcucmCriteriaContext.class)

    /**
     * Filters out baselines that were created before a baseline
     * @param name the name of the baseline
     */
    void afterBaseline(String name) {
        criteria.add(new AfterDate(CoolBaseline.get(name).date))
        log.info("Added 'afterBaseline' criteria.")
    }

    /**
     * Filters out baselines that were created before a date
     * @param format the date format
     * @param date the date
     */
    void afterDate(String format, String date) {
        criteria.add(new AfterDate(format, date))
        log.info("Added 'afterDate' criteria.")
    }

    /**
     * Filters out baselines that were created before a date
     * @param date the date
     */
    void afterDate(Date date) {
        criteria.add(new AfterDate(date))
        log.info("Added 'afterDate' criteria.")
    }

    /**
     * Filters out baselines whose name don't match the regex
     * @param regex the regex to test baseline names against
     */
    void baselineName(String regex) {
        criteria.add(new BaselineName(regex))
        log.info("Added 'baselineName' criteria.")
    }

    /**
     * Filters out baselines whose names aren't specified
     * @param names The baseline names to select
     */
    void baselineNames(String... names) {
        criteria.add(new BaselineNames(names))
        log.info("Added 'baselineNames' criteria.")
    }

    /**
     * Filters out baselines whose names aren't specified
     * @param names The baseline names to select
     */
    void baselineNames(List<String> names) {
        criteria.add(new BaselineNames(names))
        log.info("Added 'baselineNames' criteria.")
    }

    /**
     * Filters out baselines that don't have given promotion levels
     * @param levels the promotion levels to test baselines against
     */
    void promotionLevels(String... levels) {
        criteria.add(new PromotionLevels(levels))
        log.info("Added 'promotionLevels' criteria.")
    }
}
