package all2all.migration

import all2all.context.ActionsContext
import all2all.context.CriteriaContext
import all2all.context.ExtractionsContext
import all2all.migration.plan.MigrationPlan
import all2all.migration.sources.MigrationSource
import all2all.migration.targets.MigrationTarget

@Singleton
class MigrationManager {
    MigrationSource source
    MigrationTarget target
    MigrationPlan plan

    def criteriaContext
    def extractionsContext
    def actionsContext

    void migrate(boolean dryRun = false) {
        try {
            source.prepare()
            target.prepare()
            plan.fill()
            if(!dryRun)
                plan.execute()
        } finally {
            source.cleanup()
            target.cleanup()
        }
    }

    void reset() {
        source = null
        target = null
        plan = new MigrationPlan()
        criteriaContext = new CriteriaContext()
        extractionsContext = new ExtractionsContext()
        actionsContext = new ActionsContext()
    }
}
