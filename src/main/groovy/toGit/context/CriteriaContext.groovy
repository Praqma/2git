package toGit.context

import org.slf4j.LoggerFactory
import toGit.context.base.Context
import toGit.context.traits.HasCriteria
import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot

class CriteriaContext implements Context, HasCriteria {

    final static log = LoggerFactory.getLogger(this.class)

    /**
     * Filters {@link Snapshot}s using a custom Groovy closure
     * @param closure Closure to run
     */
    void custom(Closure<Boolean> closure) {
        log.debug("Registering criteria - custom")
        criteria.add(new Criteria() {
            @Override
            boolean appliesTo(Snapshot snapshot) {
                log.debug("Testing $snapshot.identifier using custom criteria.")
                closure.delegate = this
                closure.resolveStrategy = Closure.DELEGATE_FIRST
                def result = closure.call(snapshot)
                log.info("Result: " + (result ? "SUCCESS" : "FAILURE"))
                return result
            }
        })
        log.debug("Registered criteria - custom")
    }
}
