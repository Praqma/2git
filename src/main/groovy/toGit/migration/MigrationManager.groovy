package toGit.migration

import groovy.util.logging.Log
import toGit.context.ActionsContext
import toGit.context.CriteriaContext
import toGit.context.ExtractionsContext
import toGit.migration.plan.MigrationPlan
import toGit.migration.sources.MigrationSource
import toGit.migration.targets.MigrationTarget
import toGit.utils.ExceptionHelper

@Log
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
            plan.build()
            if (!dryRun)
                plan.execute()
        } catch (Exception e) {
            log.severe('An error occurred during the migration.')
            ExceptionHelper.simpleLog(e)
            log.severe('The migration has been stopped.')
            throw e
        } finally {
            log.info('Cleaning up.')
            source.cleanup()
            target.cleanup()
        }
    }
}