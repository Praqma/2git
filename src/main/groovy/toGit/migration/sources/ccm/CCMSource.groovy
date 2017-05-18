package toGit.migration.sources.ccm

import org.apache.commons.io.FileUtils

import toGit.context.base.Context
import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot
import toGit.migration.sources.MigrationSource
import toGit.migration.sources.ccm.context.CcmCriteriaContext
import toGit.migration.sources.ccm.context.CcmExtractionsContext

class CCMSource implements MigrationSource {

    String revision
    String ccm_addr
    String ccm_home
    String system_path


    @Override
    List<Snapshot> getSnapshots(List<Criteria> initialFilter) {

        List<Snapshot> projects

        // Build the CCM project conversion list
        def sout = new StringBuilder(), serr = new StringBuilder()
        def cmd_line = "bash --login " + System.getProperty("user.dir") + File.separator + "ccm-baseline-history.sh $revision"
        println cmd_line

        def envVars = System.getenv().collect { k, v -> "$k=$v" }
        def cmd = cmd_line.execute(envVars,new File(workspace))
        cmd.waitForProcessOutput(sout, serr)

        println sout
        println serr

        projects = sout.readLines().collect{new Snapshot(it){}}

        println projects.size()

        return projects
    }

    @Override
    void checkout(Snapshot snapshot) {
        copy2Filesystem(snapshot.identifier.split("@@@")[0])
    }

    private void copy2Filesystem(String project) {
        def codeFile = new File(workspace, "code")
        codeFile.parentFile.mkdirs()
        if ( ! codeFile.exists()) {
            codeFile.delete()
        }
        codeFile.mkdir()

        if ( new File(workspace + "/code/" + project).exists()){
            println "CM/Synergy checkout: Skipping project revision: ${project} - already exists"
        } else {
            def sout = new StringBuilder(), serr = new StringBuilder()

            def project_revision_with_spaces = project.replaceAll("xxx"," ")

            def file_tmp = new File(workspace + "/code/" + project + "_tmp")
            if ( file_tmp.exists() ){
                println workspace + "/code/" + project + "_tmp exist - Delete it "
                file_tmp.deleteDir()
            }

            def envVars = System.getenv().collect { k, v -> "$k=$v" }
            def cmd_line = ["ccm", "copy_to_file_system", "-p", "${project}_tmp", "-r", "${project_revision_with_spaces}:project:1"]
            println "'" + cmd_line + "'"
            def cmd = cmd_line.execute(envVars,codeFile)
            cmd.waitForProcessOutput(sout, serr)
            def exitValue = cmd.exitValue()
            println "Standard out:"
            println "'" + sout + "'"
            println "Standard error:"
            println "'" + serr + "'"
            println "Exit code: " + exitValue
            if ( exitValue ){
                throw new Exception("ccm copy_to_file_system gave an non-0 exit code" )
            }
            if ( serr.toString().readLines().size() > 0 ){
                throw new Exception("ccm copy_to_file_system standard error contains text lines: " + serr.toString().readLines().size() )
            }

            FileUtils.moveDirectory(new File(workspace + "/code/" + project + "_tmp"), new File(workspace + "/code/" + project))

        }


    }

    @Override
    void prepare() {

    }

    @Override
    void cleanup() {

    }

    @Override
    Context withCriteria(Context criteriaContext) {
        return criteriaContext as CcmCriteriaContext
    }

    @Override
    Context withExtractions(Context extractionsContext) {
        return extractionsContext as CcmExtractionsContext
    }
}
