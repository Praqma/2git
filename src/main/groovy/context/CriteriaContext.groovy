package context

import context.base.Context
import context.traits.HasCriteria
import migration.plan.Criteria
import migration.sources.Snapshot

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
    }
}
