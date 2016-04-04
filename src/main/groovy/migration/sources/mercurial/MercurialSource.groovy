package migration.sources.mercurial

import context.CriteriaContext
import context.ExtractionsContext
import context.base.Context
import migration.plan.Criteria
import migration.sources.MigrationSource
import migration.sources.Snapshot

class MercurialSource implements MigrationSource {
    @Override
    List<Snapshot> getSnapshots(List<Criteria> initialFilter) {
        return null
    }

    @Override
    void checkout(Snapshot snapshot) {

    }

    @Override
    void prepare() {

    }

    @Override
    void cleanup() {

    }

    @Override
    Context withCriteria(CriteriaContext criteriaContext) {
        return null
    }

    @Override
    Context withExtractions(ExtractionsContext extractionsContext) {
        return null
    }
}
