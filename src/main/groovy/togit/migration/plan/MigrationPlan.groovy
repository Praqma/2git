package togit.migration.plan

import org.slf4j.LoggerFactory
import togit.migration.MigrationManager

/**
 * A plan built from filters, mapping select Snapshots with extractions and actions
 */
class MigrationPlan {

    final static LOG = LoggerFactory.getLogger(this.class)

    List<Filter> filters = []

    List<Action> befores = []
    HashMap<String, SnapshotPlan> steps = [:]
    List<Action> afters = []

    /**
     * Build up the migration plan using predefined filters
     */
    void plan() {
        LOG.debug('Building migration plan')
        steps = PlanBuilder.build(filters)
        LOG.debug('Built migration plan')
        LOG.info("Constructed a migration plan for ${steps.size()} snapshots")
    }

    /**
     * Executes the migration plan.
     */
    void execute() {
        HashMap extractionMap = [:]

        LOG.info('Executing pre-migration actions')
        befores.each { action ->
            LOG.info("Executing ${action.class.simpleName}")
            action.act(extractionMap)
            LOG.info("Executed ${action.class.simpleName}")
        }
        LOG.info('Executed pre-migration actions')

        LOG.info('Executing migration')
        steps.values().each { step ->
            LOG.info("Migrating $step: ${step.extractions.size()} extractions, ${step.actions.size()} actions")

            LOG.info("Checking out $step")
            MigrationManager.instance.source.checkout(step.snapshot)
            LOG.info("Checked out $step")

            LOG.info("Executing extractions for $step")
            step.extractions.each { extraction ->
                LOG.info("Executing ${extraction.class.simpleName}")
                extraction.extract(step.snapshot).entrySet().each { k, v ->
                    extractionMap.put(k, v)
                }
                LOG.info("Executed ${extraction.class.simpleName}")
            }
            LOG.info("Executed extractions for $step")

            LOG.info("Executing actions for $step")
            step.actions.each { action ->
                LOG.info("Executing ${action.class.simpleName}")
                action.act(extractionMap)
                LOG.info("Executed ${action.class.simpleName}")
            }
            LOG.info("Executed actions for $step")
        }
        LOG.info('Executed migration')

        LOG.info('Executing post-migration actions')
        afters.each { action ->
            LOG.info("Executing ${action.class.simpleName}")
            action.act(extractionMap)
            LOG.info("Executed ${action.class.simpleName}")
        }
        LOG.info('Executed post-migration actions')
    }
}
