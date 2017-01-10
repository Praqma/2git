package toGit.migration.sources.ccbase.context

import org.slf4j.LoggerFactory
import toGit.context.base.Context
import toGit.context.traits.SourceContext
import toGit.migration.sources.ccbase.ClearCaseSource

class ClearCaseSourceContext implements Context, SourceContext {
    final static log = LoggerFactory.getLogger(this.class)

    public ClearCaseSourceContext() {
        source = new ClearCaseSource()
    }

    void configSpec(String path) {
        (source as ClearCaseSource).configSpec = path
    }

    void labelVob(String vobName) {
        (source as ClearCaseSource).labelVob = vobName
    }

    void workspace(String path) {
        source.workspace = path
    }

    void viewTag(String name) {
        (source as ClearCaseSource).viewTag = name
    }

    void vobPaths(List<String> vobPaths) {
        (source as ClearCaseSource).vobPaths = vobPaths
    }
}
