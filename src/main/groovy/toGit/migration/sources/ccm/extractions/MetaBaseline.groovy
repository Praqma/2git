package toGit.migration.sources.ccm.extractions

import org.slf4j.LoggerFactory
import toGit.migration.plan.Extraction
import toGit.migration.plan.Snapshot
import toGit.migration.sources.ccucm.Baseline

class MetaBaseline extends Extraction {

    final static log = LoggerFactory.getLogger(this.class)

    String workspace

    MetaBaseline(String ccm_workspace) {
        this.workspace = ccm_workspace
    }

    @Override
    HashMap<String, Object> extract(Snapshot snapshot ) {
        def result = [:]

        def snapshotName = snapshot.identifier.split("@@@")[0].split("~")[0]
        def snapshotRevision = snapshot.identifier.split("@@@")[0].split("~")[1]
        def baselineRevision = snapshot.identifier.split("@@@")[1].split("~")[1]

        result['snapshot'] = snapshot.identifier.split("@@@")[0]
        result['snapshotName'] = snapshotName
        result['snapshotRevision'] = snapshotRevision

        def project_revision_with_spaces = snapshot.identifier.split("@@@")[0].replaceAll("xxx"," ")

        result['snapshot_revision_wspaces'] = project_revision_with_spaces

        def envVars = System.getenv().collect { k, v -> "$k=$v" }
        def cmd_line
        def cmd
        def exitValue
        def sout
        def serr
        sout = new StringBuilder()
        serr = new StringBuilder()

        // get baseline or project baseline status of the baseline revision
        if ( baselineRevision ==~ /init/ ) {
            result['baselineRevision'] = baselineRevision
            result['baselineRevision_wstatus'] = baselineRevision
        } else {

            cmd_line = "bash --login " +
                    System.getProperty("user.dir") + File.separator + "ccm-get-status-from-baseline-or-project.sh " +
                    "$snapshotName " +
                    "$baselineRevision"
            log.info(cmd_line)
            cmd = cmd_line.execute(envVars, new File(workspace))
            cmd.waitForProcessOutput(sout, serr)
            exitValue = cmd.exitValue()
            log.info "stdout: " + sout.toString().trim()
            if (exitValue) {
                log.error "Standard error:"
                log.error "'" + serr + "'"
                log.error "Exit code: " + exitValue
                throw new Exception(cmd_line + " gave exit code: $exitValue")
            }
            if (serr.toString().readLines().size() > 0) {
                log.error "Standard error:"
                log.error "'" + serr + "'"
                log.error "Exit code: " + exitValue
                throw new Exception(cmd_line + " standard error contains text lines: " + serr.toString().readLines().size())
            }
            result['baselineRevision'] = baselineRevision
            result['baselineRevision_wstatus'] = baselineRevision + '_' + sout.toString().trim()
            sout = new StringBuilder()
            serr = new StringBuilder()
        }

        // Get the baseline date from project
//        cmd_line = 'ccm properties -f \"%{create_time[dateformat=\\\"yyyy-MM-dd HH:MM:SS\\\"]}\" ' + project_revision_with_spaces + ':project:1'
        cmd_line = "ccm properties -f \"%create_time\" $project_revision_with_spaces:project:1"
        cmd
        log.info "'" + cmd_line + "'"
        cmd = cmd_line.execute(envVars,new File(workspace))
        cmd.waitForProcessOutput(sout, serr)
        exitValue = cmd.exitValue()
        log.info "stdout: " + sout.toString().trim()
        if ( exitValue ){
            log.error "Standard error:"
            log.error "'" + serr + "'"
            log.error "Exit code: " + exitValue
            throw new Exception(cmd_line + ": gave an non-0 exit code" )
        }
        if ( serr.toString().readLines().size() > 0 ){
            log.error "Standard error:"
            log.error "'" + serr + "'"
            log.error "Exit code: " + exitValue
            throw new Exception(cmd_line + ": standard error contains text lines: " + serr.toString().readLines().size() )
        }
        result['snapshot_commiter_date'] = sout.toString().trim()
        sout = new StringBuilder()
        serr = new StringBuilder()

        // get baseline or project baseline status from
        cmd_line = "bash --login " +
                System.getProperty("user.dir") + File.separator + "ccm-get-status-from-baseline-or-project.sh " +
                "$snapshotName " +
                "$snapshotRevision"
        log.info(cmd_line)
        cmd = cmd_line.execute(envVars,new File(workspace))
        cmd.waitForProcessOutput(sout, serr)
        exitValue = cmd.exitValue()
        log.info "stdout: " + sout.toString().trim()
        if ( exitValue ){
            log.error "Standard error:"
            log.error "'" + serr + "'"
            log.error "Exit code: " + exitValue
            throw new Exception(cmd_line +": gave exit code: $exitValue" )
        }
        if ( serr.toString().readLines().size() > 0 ){
            log.error "Standard error:"
            log.error "'" + serr + "'"
            log.error "Exit code: " + exitValue
            throw new Exception(cmd_line + " standard error contains text lines: " + serr.toString().readLines().size() )
        }
        result['snapshot_status'] = sout.toString().trim()
        sout = new StringBuilder()
        serr = new StringBuilder()


        // Build the CCM project meta data for later commit
        cmd_line = "bash --login " +
                System.getProperty("user.dir") + File.separator + "ccm-extract-baseline-project-metadata.sh " +
                "$snapshotName " +
                "$snapshotRevision"
        log.info(cmd_line)
        cmd = cmd_line.execute(envVars,new File(workspace))
        cmd.waitForProcessOutput(sout, serr)
        exitValue = cmd.exitValue()
        log.info "stdout lines count: " + sout.toString().readLines().size()
        if ( exitValue ){
            log.error "Standard error:"
            log.error "'" + serr + "'"
            log.error "Exit code: " + exitValue
            throw new Exception( cmd_line + ": gave exit code: $exitValue" )
        }
        if ( serr.toString().readLines().size() > 0 ){
            log.error "Standard error:"
            log.error "'" + serr + "'"
            log.error "Exit code: " + exitValue
            throw new Exception( cmd_line + ": standard error contains text lines: " + serr.toString().readLines().size() )
        }
        result['baseline_info'] = sout

        return result
    }
}