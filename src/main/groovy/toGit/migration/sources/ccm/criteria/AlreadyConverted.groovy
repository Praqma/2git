package toGit.migration.sources.ccm.criteria

import org.slf4j.LoggerFactory
import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot


class AlreadyConverted extends Criteria {

    final static log = LoggerFactory.getLogger(this.class)

    String repo_path

    AlreadyConverted(String repo_path){
        this.repo_path = repo_path
    }



    @Override
    boolean appliesTo(Snapshot snapshot) {

        if ( new File(repo_path + "/.git" ).exists() ) {
            def snapshotRevision = snapshot.identifier.split("@@@")[0].split("~")[1]

            def sout = new StringBuilder(), serr = new StringBuilder()
            def cmd_line = "git describe " + snapshotRevision
            println cmd_line + " : in path: " + repo_path

            def envVars = []
            def cmd_process = cmd_line.execute(envVars,new File(repo_path))
            cmd_process.waitForProcessOutput(sout, serr)
    //        println sout
    //        println serr

            def exitValue = cmd_process.exitValue()
            if ( exitValue ) {
                println "Not converted - do it: "  + exitValue
                return true
            } else {
                println "Already converted - skip: " + exitValue
                return false
            }
        } else {
            println ".git not available - skipping"
            return true
        }
    }
}