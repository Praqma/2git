package migration

import context.ActionsContext
import context.CriteriaContext
import context.ExtractionsContext
import migration.plan.Action
import migration.plan.Filter
import migration.plan.SnapshotPlan
import migration.sources.MigrationSource
import migration.sources.Snapshot
import migration.targets.MigrationTarget

@Singleton
class Migrator {
    MigrationSource source
    MigrationTarget target

    List<Action> befores = []
    List<Filter> filters = []
    List<Action> afters = []

    def criteriaContext = new CriteriaContext()
    def extractionsContext = new ExtractionsContext()
    def actionsContext = new ActionsContext()

    void migrate() {
        try {
            def extractionMap = [:]

            source.prepare()
            target.prepare()

            //Build plan
            Map<String, SnapshotPlan> migrationPlan = [:]
            filters.each { filter ->
                fillMigrationPlan(migrationPlan, filter)
            }

            //Execute befores
            befores.each { action ->
                action.act(extractionMap)
            }

            //Run plan
            migrationPlan.values().each { step ->
                source.checkout(step.snapshot)
                step.extractions.each { extraction ->
                    extraction.extract(step.snapshot).entrySet().each { kv ->
                        extractionMap.put(kv.key, kv.value)
                    }
                }

                step.actions.each { action ->
                    action.act(extractionMap)
                }
            }

            //Execute afters
            afters.each { action ->
                action.act(extractionMap)
            }
        } finally {
            source.cleanup()
            target.cleanup()
        }
    }

    /**
     * Entry point for filling up the migration plan.
     * Retrieves and registers snapshots with the plan, then runs the filter.
     * @param migrationPlan The migration plan, a map of SnapshotPlans, with the Snapshot identifiers as keys
     * @param filter A top-level filter
     */
    void fillMigrationPlan(Map<String, SnapshotPlan> migrationPlan, Filter filter) {
        // Retrieve snapshots
        def snapshots = source.getSnapshots(filter.criteria)

        // Run top-level filter
        applyFilter(migrationPlan, snapshots, filter)
    }

    /**
     * Fills the migration plan by registering actions and extractions to snapshots matching its criteria
     * @param migrationPlan The migration plan, a map of SnapshotPlans, with the Snapshot identifiers as keys
     * @param snapshotPlans The set of snapshots the filter will be applied to.
     * @param filter The filter that will be applied.
     */
    void applyFilter(Map<String, SnapshotPlan> migrationPlan, Collection<Snapshot> snapshots, Filter filter) {
        def matchingSnapshots = snapshots.findAll { snapshot ->
            snapshot.matches(filter.criteria)
        }
        if (!matchingSnapshots) return

        for (def snapshot : matchingSnapshots) {
            if(!migrationPlan.containsKey(snapshot.identifier))
                migrationPlan.put(snapshot.identifier, new SnapshotPlan(snapshot))

            migrationPlan[snapshot.identifier].extractions.addAll(filter.extractions)
            migrationPlan[snapshot.identifier].actions.addAll(filter.actions)
        }

        for (def childFilter : filter.filters) {
            applyFilter(migrationPlan, matchingSnapshots, childFilter)
        }
    }
}
