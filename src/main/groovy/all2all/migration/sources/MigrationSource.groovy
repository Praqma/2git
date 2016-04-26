package all2all.migration.sources

import all2all.context.base.Context
import all2all.migration.plan.Criteria
import all2all.migration.plan.Snapshot

trait MigrationSource {
    String workspace = new File("./output/source/temp").absolutePath

    abstract List<Snapshot> getSnapshots(List<Criteria> initialFilter)

    abstract void checkout(Snapshot snapshot)

    abstract void prepare()

    abstract void cleanup()

    abstract Context withCriteria(Context criteriaContext)

    abstract Context withExtractions(Context extractionsContext)
}