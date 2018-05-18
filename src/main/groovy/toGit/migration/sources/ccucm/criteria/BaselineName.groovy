package toGit.migration.sources.ccucm.criteria

import org.slf4j.LoggerFactory
import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot
import toGit.migration.sources.ccucm.Baseline

class BaselineName extends Criteria {

    final static log = LoggerFactory.getLogger(this.class)

    String regex

    BaselineName(String regex) {
        this.regex = regex
    }

    @Override
    boolean appliesTo(Snapshot snapshot, List<Snapshot> allSnapshots) {
        def baseline = ((Baseline) snapshot).source
        log.debug("Testing '${baseline.shortname}' against regex '${regex}'")
        def matcher = baseline.shortname =~ regex
        def result = matcher.matches()
        log.debug("Result: " + (result ? "MATCH" : "no match"))
        return result
    }
}