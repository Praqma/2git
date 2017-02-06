package toGit.migration.sources.ccm

import toGit.context.base.Context
import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot
import toGit.migration.sources.MigrationSource

class CCMSource implements MigrationSource {

    String revision

    @Override
    List<Snapshot> getSnapshots(List<Criteria> initialFilter) {

        // Build the CCM project conversion list
        "./baseline_history.sh $revision".execute()
        // ems_bus~1_20131002

        List<Snapshot> projects
        projects = new File('project.txt').readLines().collect{new Snapshot(it){}}
        return projects
    }

    @Override
    void checkout(Snapshot snapshot) {
        copy2Filesystem(snapshot.identifier.split(" ")[0])
    }

    private void copy2Filesystem(String project) {
        def codeFile = new File(workspace, "code")
        codeFile.parentFile.mkdirs()
        if (codeFile.exists())
            codeFile.delete()

        println ("ccm copy_to_file_system -p $project -r \"$project:project:1\" > /dev/null".execute([],codeFile.parentFile).text)

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
