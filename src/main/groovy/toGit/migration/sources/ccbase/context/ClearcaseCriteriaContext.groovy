package toGit.migration.sources.ccbase.context

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import toGit.context.base.Context
import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot

trait ClearcaseCriteriaContext implements Context {
    final static Logger log = LoggerFactory.getLogger(ClearcaseCriteriaContext.class)

    void afterLabel(String label) {
        def labelCriteria = new Criteria() {
            @Override
            boolean appliesTo(Snapshot snapshot) {
                return label.compareTo(snapshot.identifier) < 0
            }
        }

        criteria.add(labelCriteria)
    }
}
