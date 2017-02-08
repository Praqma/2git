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

        result['snapshot'] = snapshot.identifier.split("@@@")[0]
        result['snapshotName'] = snapshot.identifier.split("@@@")[0].split("~")[0]
        result['snapshotRevision'] = snapshot.identifier.split("@@@")[0].split("~")[1]

        result['baselineRevision'] = snapshot.identifier.split("@@@")[1].split("~")[1]

        // extract ccm data (baseline, tasks bla bla)

        return result
    }
}