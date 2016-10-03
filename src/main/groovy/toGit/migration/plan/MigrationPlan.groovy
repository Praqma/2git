package toGit.migration.plan

import org.slf4j.LoggerFactory
import toGit.migration.MigrationManager

/**
 * A plan built from filters, mapping select Snapshots with extractions and actions
 */
class MigrationPlan {

    final static log = LoggerFactory.getLogger(this.class)

    List<Filter> filters = []

    List<Action> befores = []
    Map<String, SnapshotPlan> steps = [:]
    List<Action> afters = []

    /**
     * Builds the migration plan using predefined filters
     */
    void build() {
        log.debug("Building migration plan")
        steps = PlanBuilder.buildMigrationPlan(filters)
        log.debug("Built migration plan")
        log.info("Constructed a migration plan for ${steps.size()} snapshots")
    }

    /**
     * Executes the migration plan.
     */
    void execute() {
        def extractionMap = [:]

        log.info("Executing pre-migration actions")
        befores.each { action ->
            log.info("Executing ${action.class.simpleName}")
            action.act(extractionMap)
            log.info("Executed ${action.class.simpleName}")
        }
        log.info("Executed pre-migration actions")

        log.info("Executing migration")
        steps.values().each { step ->
            log.info("Migrating $step: ${step.extractions.size()} extractions, ${step.actions.size()} actions")

            log.info("Checking out $step")
            MigrationManager.instance.source.checkout(step.snapshot)
            log.info("Checked out $step")

            log.info("Executing extractions for $step")
            step.extractions.each { extraction ->
                log.info("Executing ${extraction.class.simpleName}")
                extraction.extract(step.snapshot).entrySet().each { kv ->
                    extractionMap.put(kv.key, kv.value)
                }
                log.info("Executed ${extraction.class.simpleName}")
            }
            log.info("Executed extractions for $step")

            log.info("Executing actions for $step")
            step.actions.each { action ->
                log.info("Executing ${action.class.simpleName}")
                action.act(extractionMap)
                log.info("Executed ${action.class.simpleName}")
            }
            log.info("Executed actions for $step")
        }
        log.info("Executed migration")

        log.info("Executing post-migration actions")
        afters.each { action ->
            log.info("Executing ${action.class.simpleName}")
            action.act(extractionMap)
            log.info("Executed ${action.class.simpleName}")
        }
        log.info("Executed post-migration actions")
    }
}
