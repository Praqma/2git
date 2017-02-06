package toGit.migration.sources.ccm

import toGit.context.base.Context
import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot
import toGit.migration.sources.MigrationSource

class CCMSource implements MigrationSource {

    String revision
    String ccm_addr

    @Override
    List<Snapshot> getSnapshots(List<Criteria> initialFilter) {

        // Build the CCM project conversion list
        def sout = new StringBuilder(), serr = new StringBuilder()
        def cmd = "bash /c/Users/cssr/git_conversion/utilities_for_jira_git_conversion/baseline_history.sh $revision".execute()
        cmd.consumeProcessOutput(sout, serr)
        cmd.waitForProcessOutput()

        // ems_bus~1_20131002

        List<Snapshot> projects
        projects = sout.collect{new Snapshot(it){}}

        return projects
    }

    @Override
    void checkout(Snapshot snapshot) {
        copy2Filesystem(snapshot.identifier.split(" ")[0])
    }

    private void copy2Filesystem(String project) {
        def codeFile = new File(workspace, "code")
        codeFile.parentFile.mkdirs()
        if (codeFile.exists()) {
            codeFile.delete()
        }
        codeFile.mkdir()

        def sout = new StringBuilder(), serr = new StringBuilder()
        def envVars = ["CCM_ADDR=" + ccm_addr ];
        def cmd = "ccm copy_to_file_system -p $project -r \"$project:project:1\" ".execute(envVars,codeFile)
        cmd.consumeProcessOutput(sout, serr)
        cmd.waitForProcessOutput()
        println sout
        println serr
        //println ("ccm copy_to_file_system -p $project -r \"$project:project:1\" > /dev/null".execute([],codeFile.parentFile).text)

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
