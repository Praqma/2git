package all2all.migration.sources.dummy

import all2all.context.base.Context
import all2all.migration.plan.Criteria
import all2all.migration.plan.Snapshot
import all2all.migration.sources.MigrationSource

class DummySource implements MigrationSource {
    @Override
    List<Snapshot> getSnapshots(List<Criteria> initialFilter) {
        return [
                new Snapshot("foo") {},
                new Snapshot("bar") {},
                new Snapshot("baz") {}
        ]
    }

    @Override
    void checkout(Snapshot snapshot) {
        writeDummyFile(snapshot.identifier)
    }

    private void writeDummyFile(String contents) {
        def codeFile = new File(workspace, "code")
        codeFile.parentFile.mkdirs()
        if (codeFile.exists())
            codeFile.delete()
        codeFile.createNewFile()
        codeFile.write(contents)
    }

    @Override
    void prepare() {

    }

    @Override
    void cleanup() {

    }

    @Override
    Context withCriteria(Context criteriaContext) {
        return criteriaContext
    }

    @Override
    Context withExtractions(Context extractionsContext) {
        return extractionsContext
    }
}
