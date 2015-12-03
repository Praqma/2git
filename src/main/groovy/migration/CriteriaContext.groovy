package migration


@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
import net.praqma.clearcase.ucm.entities.Baseline

@Slf4j
class CriteriaContext {
    List<Criteria> criteria = []

    def void baselineName(String regex) {
        criteria.add(new Criteria() {
            @Override
            boolean appliesTo(Baseline baseline) {
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
            boolean appliesTo(Baseline baseline) {
                println "Testing '" + baseline.shortname + "(" + baseline.promotionLevel + ")' against promotionLevels '" + promotionLevels + "'."
                def result = promotionLevels.contains(baseline.promotionLevel.toString())
                println "Result: " + (result ? "SUCCESS" : "FAILURE")
                return result
            }
        })
    }

    def void since (String format, String date) {
        since(Date.parseToStringDate(format, date))
    }

    def void since(Date date) {
        criteria.add(new Criteria() {
            @Override
            boolean appliesTo(Baseline baseline) {
                println "Testing '" + baseline.shortname + "(" + baseline.date + ")' against date '" + date+ "'."
                def result = baseline.date >= date
                println "Result: " + (result ? "SUCCESS" : "FAILURE")
                return result
            }
        })
    }
}
