package togit.migration.plan

import org.slf4j.LoggerFactory
import togit.migration.MigrationManager

class PlanBuilder {

    final static LOG = LoggerFactory.getLogger(this.class)

    /**
     * Builds the migration plan for the given source and filters.
     * @param source the Migration
     * @param filters the Filters to build the migration plan for
     * @return the migration plan as a HashMap<Snapshot.Id, SnapshotPlan>
     */
    static HashMap build(List<Filter> filters) {
        HashMap<String, SnapshotPlan> migrationPlan = [:]
        filters.each { filter ->
            fill(migrationPlan, filter)
        }
        migrationPlan
    }

    /**
     * Retrieves and registers snapshots with the plan, then runs the filter.
     * @param migrationPlan The migration plan, a map of SnapshotPlans, with the Snapshot identifiers as keys
     * @param filter A top-level filter
     */
    private static void fill(HashMap<String, SnapshotPlan> migrationPlan, Filter filter) {
        LOG.debug('Retrieving snapshots')
        List snapshots = MigrationManager.instance.source.getSnapshots(filter.criteria)
        LOG.debug("Retrieved ${snapshots.size()} snapshots")

        apply(migrationPlan, snapshots, filter)
    }

    /**
     * Fills the migration plan by registering actions and extractions to snapshots matching its criteria
     * @param migrationPlan The migration plan, a map of SnapshotPlans, with the Snapshot identifiers as keys
     * @param snapshotPlans The set of snapshots the filter will be applied to.
     * @param filter The filter that will be applied.
     */
    private static void apply(HashMap<String, SnapshotPlan> migrationPlan, List<Snapshot> snapshots, Filter filter) {
        LOG.debug("Applying filter to ${snapshots.size()}")
        List matchingSnapshots = snapshots.findAll { snapshot ->
            snapshot.matches(filter.criteria, snapshots)
        }
        LOG.debug("Found ${matchingSnapshots.size()} matching snapshots")
        if (!matchingSnapshots) { return }

        LOG.debug("Applying ${filter.extractions.size()} extractions and ${filter.actions.size()} actions to ${matchingSnapshots.size()} snapshots")
        for (Snapshot snapshot : matchingSnapshots) {
            if (!migrationPlan.containsKey(snapshot.identifier)) {
                migrationPlan.put(snapshot.identifier, new SnapshotPlan(snapshot))
            }
            migrationPlan[snapshot.identifier].extractions.addAll(filter.extractions)
            migrationPlan[snapshot.identifier].actions.addAll(filter.actions)
        }

        if (!filter.filters) { return }
        LOG.debug('Applying subfilters')
        for (Filter childFilter : filter.filters) {
            apply(migrationPlan, matchingSnapshots, childFilter)
        }
    }
}
