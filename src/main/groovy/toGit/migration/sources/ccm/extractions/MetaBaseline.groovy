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
    HashMap<String, Object> extract(Snapshot snapshot) {
        def result = [:]

        // extract ccm data (baseline, tasks bla bla)

        return result
    }
}