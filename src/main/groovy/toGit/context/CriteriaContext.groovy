package toGit.context

import toGit.context.base.Context
import toGit.context.traits.HasCriteria
import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot
import groovy.util.logging.Log

@Log
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
