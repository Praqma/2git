package migration.sources

import context.CriteriaContext
import context.ExtractionsContext
import context.base.Context
import migration.plan.Criteria

interface MigrationSource {
    List<Snapshot> getSnapshots(List<Criteria> initialFilter)

    void checkout(Snapshot snapshot)

    void prepare()

    void cleanup()

    Context withCriteria(CriteriaContext criteriaContext)

    Context withExtractions(ExtractionsContext extractionsContext)
}