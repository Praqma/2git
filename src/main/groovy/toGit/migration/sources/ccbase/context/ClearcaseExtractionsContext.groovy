package toGit.migration.sources.ccbase.context

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import toGit.context.base.Context
import toGit.migration.plan.Extraction
import toGit.migration.plan.Snapshot
import toGit.migration.sources.ccucm.extractions.BaselineProperty

trait ClearcaseExtractionsContext implements Context {
    final static Logger log = LoggerFactory.getLogger(ClearcaseExtractionsContext.class)

    void label(String key) {
        def labelExtraction = new Extraction() {
            @Override
            HashMap<String, Object> extract(Snapshot snapshot) {
                def l = "cleartool getlabel".execute().text
                return [key: l]
            }
        }
        extractions.add(labelExtraction)
    }
}
