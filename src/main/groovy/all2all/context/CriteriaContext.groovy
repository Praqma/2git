package all2all.context

import all2all.context.base.Context
import all2all.context.traits.HasCriteria
import all2all.migration.plan.Criteria
import all2all.migration.plan.Snapshot
import groovy.util.logging.Slf4j

@Slf4j
class CriteriaContext implements Context, HasCriteria {

    /**
     * Filters {@link Snapshot}s using a custom Groovy closure
     * @param closure Closure to run
     */
    void custom(Closure<Boolean> closure) {
        criteria.add(new Criteria() {
            @Override
            boolean appliesTo(Snapshot snapshot) {
                println "Testing $snapshot.identifier using custom criteria."
                closure.delegate = this
                closure.resolveStrategy = Closure.DELEGATE_FIRST
                def result = closure.call(snapshot)
                println "Result: " + (result ? "SUCCESS" : "FAILURE")
                return result
            }
        })
        log.info("Added 'custom' criteria.")
    }
}
