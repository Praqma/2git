package migration.sources

import context.CriteriaContext
import context.ExtractionsContext
import context.base.Context
import migration.plan.Criteria

trait MigrationSource {
    String workspace = new File("./output/source/temp").absolutePath

    abstract List<Snapshot> getSnapshots(List<Criteria> initialFilter)

    abstract void checkout(Snapshot snapshot)

    abstract void prepare()

    abstract void cleanup()

    abstract Context withCriteria(CriteriaContext criteriaContext)

    abstract Context withExtractions(ExtractionsContext extractionsContext)
}