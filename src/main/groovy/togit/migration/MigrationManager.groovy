package togit.migration

import org.slf4j.LoggerFactory
import togit.context.ActionsContext
import togit.context.CriteriaContext
import togit.context.ExtractionsContext
import togit.context.Context
import togit.migration.plan.MigrationPlan
import togit.migration.sources.MigrationSource
import togit.migration.targets.MigrationTarget
import togit.utils.ExceptionHelper

@Singleton
class MigrationManager {

    final static LOG = LoggerFactory.getLogger(this.class)

    MigrationSource source
    HashMap<String, MigrationTarget> targets = [:]
    MigrationPlan plan

    Context criteriaContext
    Context extractionsContext
    Context actionsContext

    void reset() {
        source = null
        targets.clear()
        plan = new MigrationPlan()
        refreshContexts()
    }

    void refreshContexts() {
        criteriaContext = new CriteriaContext()
        extractionsContext = new ExtractionsContext()
        actionsContext = new ActionsContext()
    }

    void resetMigrationPlan() {
        plan = new MigrationPlan()
    }

    void migrate(boolean dryRun = false) {
        if (dryRun) {
            plan.plan()
            return
        }

        try {
            LOG.info('Preparing source')
            source.prepare()
            LOG.info('Prepared source')
            LOG.info('Preparing targets')
            targets.values().each { t -> t.prepare() }
            LOG.info('Prepared targets')
            plan.plan()
            LOG.info('Executing migration plan')
            plan.execute()
            LOG.info('Executed migration plan')
        } catch (Exception e) {
            LOG.error('An error occurred during the migration')
            ExceptionHelper.simpleLog(e)
            LOG.error('The migration has been stopped')
            throw e
        } finally {
            LOG.info('Cleaning up source')
            source.cleanup()
            LOG.info('Cleaned up source')
            LOG.info('Cleaning up targets')
            targets.values().each { t -> t.cleanup() }
            LOG.info('Cleaned up targets')
        }
    }
}
