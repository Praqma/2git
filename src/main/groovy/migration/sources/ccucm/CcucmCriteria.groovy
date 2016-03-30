package migration.sources.ccucm

import migration.plan.Criteria
import migration.sources.Snapshot

class CcucmCriteria {

    class AfterDate extends Criteria {
        Date date

        AfterDate(String format, String date) {
            this.date = Date.parse(format, date)
        }

        AfterDate(Date date) {
            this.date = date
        }

        @Override
        boolean appliesTo(Snapshot snapshot) {
            def baseline = ((Baseline) snapshot).source
            println "Testing '" + baseline.shortname + " (" + baseline.date + ")' against date '" + date + "'."
            def result = baseline.date > date
            println "Result: " + (result ? "SUCCESS" : "FAILURE")
            return result
        }
    }

    class BaselineName extends Criteria {
        String regex

        BaselineName(String regex){
            this.regex = regex
        }

        @Override
        boolean appliesTo(Snapshot snapshot) {
            def baseline = ((Baseline) snapshot).source
            println "Testing '" + baseline.shortname + "' against regex '" + regex + "'."
            def matcher = baseline.shortname =~ regex
            def result = matcher.matches()
            println "Result: " + (result ? "SUCCESS" : "FAILURE")
            return result
        }
    }

    class PromotionLevels extends Criteria {
        String[] levels

        PromotionLevels(String... levels){
            this.levels = levels
        }

        @Override
        boolean appliesTo(Snapshot snapshot) {
            def baseline = ((Baseline) snapshot).source
            println "Testing '" + baseline.shortname + " (" + baseline.promotionLevel + ")' against promotionLevels '" + promotionLevels + "'."
            def result = levels.contains(baseline.promotionLevel.toString())
            println "Result: " + (result ? "SUCCESS" : "FAILURE")
            return result
        }
    }
}
