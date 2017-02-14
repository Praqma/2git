package toGit.migration.sources.ccm.criteria

import org.slf4j.LoggerFactory
import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot


class AlreadyConverted extends Criteria {

    final static log = LoggerFactory.getLogger(this.class)



    AlreadyConverted( ){

    }

    @Override
    boolean appliesTo(Snapshot snapshot) {

        def sout = new StringBuilder(), serr = new StringBuilder()
        def cmd_line = "git descibe " + snapshot.baselineRevision
        println cmd_line

        def cmd_process = cmd_line.execute(envVars,new File(target.workspace))
        cmd_process.waitForProcessOutput(sout, serr)
        println sout
        println serr

        def exitValue = cmd_process.exitValue()
        log.debug("Result: " + (exitValue ? "Already converted - skip" : "Not converted - do it"))
        if ( exitValue )
            return true
        else
            return false
    }
}