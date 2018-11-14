package togit.migration.sources

import togit.migration.plan.Criteria
import togit.migration.plan.Snapshot

trait MigrationSource {
    /**
     * The workspace where Snapshots will be checked out into
     */
    String workspace = new File('./output/source/temp').absolutePath

    /**
     * Fetches all relevant Snapshots from the source
     * @param initialFilter optional top-level filter used for server-side filtering
     * @return A List of relevant Snapshots
     */
    abstract List<Snapshot> getSnapshots(List<Criteria> initialFilter)

    /**
     * Checks out the given Snapshot
     * @param snapshot the Snapshot to check out
     */
    abstract void checkout(Snapshot snapshot)

    /**
     * Does any pre-migration setup
     */
    abstract void prepare()

    /**
     * Cleans up any migration leftovers
     */
    abstract void cleanup()

    /**
     * Adds source-specific contexts to the global CriteriaContext
     */
    abstract void mixinCriteria()

    /**
     * Adds source-specific contexts to the global ExtractionsContext
     */
    abstract void mixinExtractions()
}
