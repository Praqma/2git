package migration

import context.ActionsContext
import context.CriteriaContext
import context.ExtractionsContext
import migration.plan.Action
import migration.plan.Filter
import migration.plan.SnapshotPlan
import migration.sources.MigrationSource
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

    def migrate() {
        def extractionMap = [:]

        source.prepare()
        target.prepare()

        //Build plan
        Map<String, SnapshotPlan> migrationPlan = [:]
        filters.each { filter ->
            buildMigrationPlan(migrationPlan, filter)
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

        source.cleanup()
        target.cleanup()
    }

    void buildMigrationPlan(Map<String, SnapshotPlan> migrationPlan, Filter filter) {
        def snapshots = source.getSnapshots(filter.criteria)
        snapshots.each { snapshot ->
            if (!migrationPlan.containsKey(snapshot.identifier))
                migrationPlan.put(snapshot.identifier, new SnapshotPlan(snapshot))
        }

        buildMigrationPlan(migrationPlan, migrationPlan.values().toList(), filter)
    }

    void buildMigrationPlan(Map<String, SnapshotPlan> migrationPlan, List<SnapshotPlan> snapshotPlans, Filter filter) {
        def matchingSnapshotPlans = snapshotPlans.findAll { snapshotPlan ->
            snapshotPlan.matches(filter.criteria)
        }
        if (!matchingSnapshotPlans) return

        for (def snapshotPlan : matchingSnapshotPlans) {
            snapshotPlan.extractions.addAll(filter.extractions)
            snapshotPlan.actions.addAll(filter.actions)
        }

        for (def childFilter : filter.filters) {
            buildMigrationPlan(migrationPlan, matchingSnapshotPlans, childFilter)
        }
    }
}
