package migration.sources.ccucm.context

import context.base.Context
import migration.sources.ccucm.criteria.AfterDate
import migration.sources.ccucm.criteria.BaselineName
import migration.sources.ccucm.criteria.PromotionLevels
import net.praqma.clearcase.ucm.entities.Baseline as CoolBaseline

trait CcucmCriteriaContext implements Context {

    /**
     * Filters out baselines that were created before a baseline
     * @param name the name of the baseline
     */
    void afterBaseline(String name) {
        criteria.add(new AfterDate(CoolBaseline.get(name).date))
    }

    /**
     * Filters out baselines that were created before a date
     * @param format the date format
     * @param date the date
     */
    void afterDate(String format, String date) {
        criteria.add(new AfterDate(format, date))
    }

    /**
     * Filters out baselines that were created before a date
     * @param date the date
     */
    void afterDate(Date date) {
        criteria.add(new AfterDate(date))
    }

    /**
     * Filters out baselines whose name don't match the regex
     * @param regex the regex to test baseline names against
     */
    void baselineName(String regex) {
        criteria.add(new BaselineName(regex))
    }

    /**
     * Filters out baselines that don't have given promotion levels
     * @param levels the promotion levels to test baselines against
     */
    void promotionLevels(String... levels) {
        criteria.add(new PromotionLevels(levels))
    }
}
