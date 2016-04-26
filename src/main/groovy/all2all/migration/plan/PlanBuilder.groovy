package all2all.migration.plan

import all2all.migration.MigrationManager

class PlanBuilder {

    /**
     * Builds the migration plan for the given source and filters.
     * @param source the Migration
     * @param filters the Filters to build the migration plan for
     * @return the migration plan as a Map<Snapshot.Id, SnapshotPlan>
     */
    static def buildMigrationPlan(List<Filter> filters) {
        Map<String, SnapshotPlan> migrationPlan = [:]
        filters.each { filter ->
            fillMigrationPlan(migrationPlan, filter)
        }
        return migrationPlan
    }

    /**
     * Retrieves and registers snapshots with the plan, then runs the filter.
     * @param migrationPlan The migration plan, a map of SnapshotPlans, with the Snapshot identifiers as keys
     * @param filter A top-level filter
     */
    private static void fillMigrationPlan(Map<String, SnapshotPlan> migrationPlan, Filter filter) {
        // Retrieve snapshots
        def snapshots = MigrationManager.instance.source.getSnapshots(filter.criteria)

        // Run top-level filter
        applyFilter(migrationPlan, snapshots, filter)
    }

    /**
     * Fills the migration plan by registering actions and extractions to snapshots matching its criteria
     * @param migrationPlan The migration plan, a map of SnapshotPlans, with the Snapshot identifiers as keys
     * @param snapshotPlans The set of snapshots the filter will be applied to.
     * @param filter The filter that will be applied.
     */
    private
    static void applyFilter(Map<String, SnapshotPlan> migrationPlan, Collection<Snapshot> snapshots, Filter filter) {
        def matchingSnapshots = snapshots.findAll { snapshot ->
            snapshot.matches(filter.criteria)
        }
        if (!matchingSnapshots) return

        for (def snapshot : matchingSnapshots) {
            if (!migrationPlan.containsKey(snapshot.identifier))
                migrationPlan.put(snapshot.identifier, new SnapshotPlan(snapshot))

            migrationPlan[snapshot.identifier].extractions.addAll(filter.extractions)
            migrationPlan[snapshot.identifier].actions.addAll(filter.actions)
        }

        for (def childFilter : filter.filters) {
            applyFilter(migrationPlan, matchingSnapshots, childFilter)
        }
    }
}
