package toGit.migration

import toGit.context.ActionsContext
import toGit.context.CriteriaContext
import toGit.context.ExtractionsContext
import toGit.migration.plan.MigrationPlan
import toGit.migration.sources.MigrationSource
import toGit.migration.targets.MigrationTarget

@Singleton
class MigrationManager {
    MigrationSource source
    MigrationTarget target
    MigrationPlan plan

    def criteriaContext
    def extractionsContext
    def actionsContext

    void reset() {
        source = null
        target = null
        plan = new MigrationPlan()
        criteriaContext = new CriteriaContext()
        extractionsContext = new ExtractionsContext()
        actionsContext = new ActionsContext()
    }

    void migrate(boolean dryRun = false) {
        try {
            source.prepare()
            target.prepare()
            plan.fill()
            if (!dryRun)
                plan.execute()
        } finally {
            source.cleanup()
            target.cleanup()
        }
    }
}