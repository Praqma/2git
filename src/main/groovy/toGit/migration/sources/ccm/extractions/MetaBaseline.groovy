package toGit.migration.sources.ccm.extractions

import org.slf4j.LoggerFactory
import toGit.migration.plan.Extraction
import toGit.migration.plan.Snapshot
import toGit.migration.sources.ccucm.Baseline

class MetaBaseline extends Extraction {

    final static log = LoggerFactory.getLogger(this.class)

    Map<String, String> map

    MetaBaseline(Map<String, String> map) {
        this.map = map
    }

    @Override
    HashMap<String, Object> extract(Snapshot snapshot, Sting[] submodule_paths) {
        def result = [:]

        result['snapshot'] = snapshot.identifier.split("@@@")[0]
        result['snapshotName'] = snapshot.identifier.split("@@@")[0].split("~")[0]
        result['snapshotRevision'] = snapshot.identifier.split("@@@")[0].split("~")[1]

        result['baselineRevision'] = snapshot.identifier.split("@@@")[1].split("~")[1]

        def submodules = [:]

        submodule_paths.each { path ->
            log.info("Submodule path: ${path}")
            def sout = new StringBuilder(), serr = new StringBuilder()
            def cmd_line = 'ccm query "is_member_of(\'' + snapshot.identifier.split("@@@")[0] + 'project:1\') and type='project' and name=\'' + path + "'" + ' -u -f "%displayname" '
            println cmd_line

            def envVars = ["CCM_ADDR=" + ccm_addr ];
            def cmd = cmd_line.execute(envVars,new File(workspace))
            //cmd.consumeProcessOutput(sout, serr)
            cmd.waitForProcessOutput(sout, serr)

            println sout
            println serr

            submodule_line = sout.readLines().first().trim()
            submodules[submodule_line.split('~')[0]] =  submodule_line.split('~')[1]
            log.info("Submodule revision: ${submodule_line}")
        }

        result['submodules'] = submodules

        // extract ccm data (baseline, tasks bla bla)

        return result
    }
}