package toGit.migration.sources.dummy

import toGit.context.Context
import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot
import toGit.migration.sources.MigrationSource

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
