package toGit.migration

import groovy.util.logging.Log
import toGit.context.ActionsContext
import toGit.context.CriteriaContext
import toGit.context.ExtractionsContext
import toGit.context.base.Context
import toGit.migration.plan.MigrationPlan
import toGit.migration.sources.MigrationSource
import toGit.migration.targets.MigrationTarget
import toGit.utils.ExceptionHelper

@Log
@Singleton
class MigrationManager {
    MigrationSource source
    LinkedHashMap<String, MigrationTarget> targets = []
    MigrationPlan plan

    Context criteriaContext
    Context extractionsContext
    Context actionsContext

    void reset() {
        source = null
        targets = []
        plan = new MigrationPlan()
        criteriaContext = new CriteriaContext()
        extractionsContext = new ExtractionsContext()
        actionsContext = new ActionsContext()
    }

    void migrate(boolean dryRun = false) {
        try {
            source.prepare()
            targets.values().each {t -> t.prepare()}
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
            targets.values().each {t -> t.cleanup()}
        }
    }
}