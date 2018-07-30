package toGit.migration

import org.slf4j.LoggerFactory
import toGit.context.ActionsContext
import toGit.context.CriteriaContext
import toGit.context.ExtractionsContext
import toGit.context.Context
import toGit.migration.plan.MigrationPlan
import toGit.migration.sources.MigrationSource
import toGit.migration.targets.MigrationTarget
import toGit.utils.ExceptionHelper

@Singleton
class MigrationManager {

    final static log = LoggerFactory.getLogger(this.class)

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

    void resetMigrationPlan() {
        plan = new MigrationPlan()
    }

    void migrate(boolean dryRun = false) {
        if(dryRun) {
            plan.build()
            return
        }
        try {
            log.info("Preparing source")
            source.prepare()
            log.info("Prepared source")
            log.info("Preparing targets")
            targets.values().each { t -> t.prepare() }
            log.info("Prepared targets")
            plan.build()
            log.info("Executing migration plan")
            plan.execute()
            log.info("Executed migration plan")
        } catch (Exception e) {
            log.error('An error occurred during the migration')
            ExceptionHelper.simpleLog(e)
            log.error('The migration has been stopped')
            throw e
        } finally {
            log.info('Cleaning up source')
            source.cleanup()
            log.info('Cleaned up source')
            log.info('Cleaning up targets')
            targets.values().each { t -> t.cleanup() }
            log.info('Cleaned up targets')
        }
    }
}