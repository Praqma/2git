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
        def cmd_line = "bash --login " + System.getProperty("user.dir") + File.separator + "baseline_history.sh $revision"
        println cmd_line

        def envVars = System.getenv().collect { k, v -> "$k=$v" }
//        def envVars = ["CCM_ADDR=" + ccm_addr, "CCM_HOME=" + ccm_home, "PATH=" + system_path]
        def cmd = cmd_line.execute(envVars,new File(workspace))
        //cmd.consumeProcessOutput(sout, serr)
        cmd.waitForProcessOutput(sout, serr)

        println sout
        println serr

        projects = sout.readLines().collect{new Snapshot(it){}}

//        projects.each {
//            println it
//        }

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
        if (codeFile.exists()) {
            codeFile.delete()
        }
        codeFile.mkdir()

        if ( new File(workspace + "/code/" + project).exists()){
            println "Skipping project revision: ${project} - already exists"
        } else {
            def sout = new StringBuilder(), serr = new StringBuilder()

            def project_revision_with_spaces = project.replaceAll("xxx"," ")

            if ( new File( workspace + "/code/" + project + "_tmp" ).exists() ){
                new File(workspace + "/code/" + project + "_tmp").delete()
            }

            def envVars = System.getenv().collect { k, v -> "$k=$v" }
            //def envVars = ["CCM_ADDR=" + ccm_addr, "CCM_HOME=" + ccm_home, "PATH=" + system_path]
            def cmd_line = ["ccm", "copy_to_file_system", "-p", "${project}_tmp", "-r", "${project_revision_with_spaces}:project:1"]
            println "'" + cmd_line + "'"
            def cmd = cmd_line.execute(envVars,codeFile)
            cmd.waitForProcessOutput(sout, serr)
            println sout
            println serr
            FileUtils.moveDirectory(new File(workspace + "/code/" + project + "_tmp"), new File(workspace + "/code/" + project))

            //println ("ccm copy_to_file_system -p $project -r \"$project:project:1\" > /dev/null".execute([],codeFile.parentFile).text)
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
