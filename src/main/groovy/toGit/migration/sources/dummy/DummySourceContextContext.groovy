package toGit.migration.sources.dummy

import toGit.context.base.Context
import toGit.context.traits.SourceContext

class DummySourceContextContext implements Context, SourceContext {
    public DummySourceContextContext() {
        source = new DummySource()
    }
}
