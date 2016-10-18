package toGit.migration.sources.ccbase.context

import groovy.util.logging.Log
import toGit.context.base.Context
import toGit.context.traits.SourceContext
import toGit.migration.sources.ccbase.ClearcaseSource

@Log
class ClearcaseSourceContext implements Context, SourceContext {

    public ClearcaseSourceContext() {
        source = new ClearcaseSource()
    }

    void configSpec(String path) {
        (source as ClearcaseSource).configSpec = path
    }

    void labelVob(String vobName) {
        (source as ClearcaseSource).labelVob = vobName
    }

    void workspace(String path) {
        source.workspace = path
    }

    void viewTag(String name) {
        (source as ClearcaseSource).viewTag = name
    }

    void vobPaths(List<String> vobPaths) {
        (source as ClearcaseSource).vobPaths = vobPaths
    }
}
