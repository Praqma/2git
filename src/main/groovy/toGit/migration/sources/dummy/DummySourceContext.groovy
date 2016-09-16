package toGit.migration.sources.dummy

import toGit.context.base.Context
import toGit.context.traits.SourceContext

class DummySourceContext implements Context, SourceContext {
    public DummySourceContext() {
        source = new DummySource()
    }
}
