package toGit.migration.sources.dummy

import toGit.context.SourceContext

class DummySourceContext extends SourceContext {
    public DummySourceContext() {
        source = new DummySource()
    }
}
