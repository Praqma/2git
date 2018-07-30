package togit.migration.sources.dummy

import togit.context.CriteriaContext
import togit.context.ExtractionsContext
import togit.migration.plan.Criteria
import togit.migration.plan.Snapshot
import togit.migration.sources.MigrationSource

class DummySource implements MigrationSource {
    @Override
    List<Snapshot> getSnapshots(List<Criteria> initialFilter) {
        [
            new Snapshot('foo') { } ,
            new Snapshot('bar') { } ,
            new Snapshot('baz') { } ,
        ]
    }

    @Override
    void checkout(Snapshot snapshot) {
        writeDummyFile(snapshot.identifier)
    }

    private void writeDummyFile(String contents) {
        File codeFile = new File(workspace, 'code')
        codeFile.parentFile.mkdirs()
        if (codeFile.exists()) {
            codeFile.delete()
        }
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
    void addCriteria() {
        CriteriaContext.mixin(DummyCriteriaContext)
    }

    @Override
    void addExtractions() {
        ExtractionsContext.mixin(DummyExtractionsContext)
    }
}
