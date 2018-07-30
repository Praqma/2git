package togit.context

import org.slf4j.LoggerFactory
import togit.migration.plan.Criteria
import togit.migration.plan.Snapshot

class CriteriaContext implements Context {

    final static LOG = LoggerFactory.getLogger(this.class)

    final List<Criteria> criteria = []

    /**
     * Filters {@link Snapshot}s using a custom Groovy closure
     * @param closure Closure to run
     */
    void custom(Closure<Boolean> closure) {
        LOG.debug('Registering criteria - custom')
        criteria.add(new Criteria() {
            @Override
            boolean appliesTo(Snapshot snapshot, List<Snapshot> allSnapshots) {
                LOG.debug("Testing $snapshot.identifier using custom criteria.")
                closure.delegate = this
                closure.resolveStrategy = Closure.DELEGATE_FIRST
                boolean result = closure.call(snapshot, allSnapshots)
                LOG.info('Result: ' + (result ? 'SUCCESS' : 'FAILURE'))
                result
            }
        })
        LOG.debug('Registered criteria - custom')
    }
}
