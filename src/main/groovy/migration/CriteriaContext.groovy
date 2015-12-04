package migration

@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
import net.praqma.clearcase.ucm.entities.Baseline as CoolBaseline

@Slf4j
class CriteriaContext {
    List<Criteria> criteria = []

    def void baselineName(String regex) {
        criteria.add(new Criteria() {
            @Override
            boolean appliesTo(CoolBaseline baseline) {
                println "Testing '" + baseline.shortname + "' against regex '" + regex + "'."
                def matcher = baseline.shortname =~ regex
                def result = matcher.matches()
                println "Result: " + (result ? "SUCCESS" : "FAILURE")
                return result
            }
        })
    }

    def void promotionLevels(String... promotionLevels) {
        criteria.add(new Criteria() {
            @Override
            boolean appliesTo(CoolBaseline baseline) {
                println "Testing '" + baseline.shortname + " (" + baseline.promotionLevel + ")' against promotionLevels '" + promotionLevels + "'."
                def result = promotionLevels.contains(baseline.promotionLevel.toString())
                println "Result: " + (result ? "SUCCESS" : "FAILURE")
                return result
            }
        })
    }

    def void afterBaseline(String name){
        afterDate(CoolBaseline.get(name).date)
    }

    def void afterDate(String format, String date) {
        afterDate(Date.parseToStringDate(format, date))
    }

    def void afterDate(Date date) {
        criteria.add(new Criteria() {
            @Override
            boolean appliesTo(CoolBaseline baseline) {
                println "Testing '" + baseline.shortname + " (" + baseline.date + ")' against date '" + date+ "'."
                def result = baseline.date > date
                println "Result: " + (result ? "SUCCESS" : "FAILURE")
                return result
            }
        })
    }
}
