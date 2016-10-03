package toGit.migration.sources.ccbase.context

import groovy.util.logging.Log
import org.slf4j.LoggerFactory
import toGit.context.base.Context
import toGit.context.traits.SourceContext
import toGit.migration.sources.ccbase.ClearcaseSource

@Log
class ClearcaseSourceContext implements Context, SourceContext {

    public ClearcaseSourceContext() {
        source = new ClearcaseSource()
    }

    void configSpec(String path) {
        (source as ClearcaseSource).configSpecPath = path
    }

    void workspace(String path) {
        source.workspace = path
    }
}
