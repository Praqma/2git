package migration.sources

import context.CriteriaContext
import context.ExtractionsContext
import migration.plan.Criteria

interface MigrationSource {
    List<Snapshot> getSnapshots(List<Criteria> initialFilter)

    void checkout(Snapshot snapshot)

    void prepare()

    void cleanup()

    CriteriaContext withCriteria(CriteriaContext criteriaContext)

    ExtractionsContext withExtractions(ExtractionsContext extractionsContext)
}