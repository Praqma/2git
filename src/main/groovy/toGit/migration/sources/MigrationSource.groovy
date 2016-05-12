package toGit.migration.sources

import toGit.context.base.Context
import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot

trait MigrationSource {
    String workspace = new File("./output/source/temp").absolutePath

    abstract List<Snapshot> getSnapshots(List<Criteria> initialFilter)

    abstract void checkout(Snapshot snapshot)

    abstract void prepare()

    abstract void cleanup()

    abstract Context withCriteria(Context criteriaContext)

    abstract Context withExtractions(Context extractionsContext)
}