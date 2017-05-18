package toGit.migration.sources.ccm.criteria

import org.slf4j.LoggerFactory
import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot


class AlreadyConverted extends Criteria {

    final static log = LoggerFactory.getLogger(this.class)

    String repo_path
    def tags = []

    AlreadyConverted(String repo_path){
        this.repo_path = repo_path
        if ( new File(repo_path + File.separator + ".git" ).exists() ) {

            def sout = new StringBuilder(), serr = new StringBuilder()
            def cmd_line = "git tag"
            log.info cmd_line + " : in path: " + repo_path

            def envVars = []
            def cmd_process = cmd_line.execute(envVars,new File(repo_path))
            cmd_process.waitForProcessOutput(sout, serr)

            def exitValue = cmd_process.exitValue()
            if ( exitValue ) {
                throw new Exception('WHAT??')
            }

            log.info "Read the tags from the git tag output"
            sout.eachLine { line ->
                tags << line
            }
            log.info "Done"

            tags.each {
                log.info it
            }
        } else {
            log.info ('.git not available - assuming the repo is not initialized yet - skip')
        }
    }

    @Override
    boolean appliesTo(Snapshot snapshot) {
        def snapshotRevision = snapshot.identifier.split("@@@")[0].split("~")[1]

        def tag_regex = snapshotRevision + "_" + "[dprtis][eueenq][lblsta]\$"

        boolean convert = true
        tags.each { tag ->
            if ( tag ==~ /$tag_regex/ ) {
                println("Already converted - skip: " + snapshot.identifier + " ~ " + tag_regex )
                convert = false
            }
        }
        println ("Not converted - do it: " + snapshot.identifier + " ~ " + tag_regex )
        return convert
    }
}