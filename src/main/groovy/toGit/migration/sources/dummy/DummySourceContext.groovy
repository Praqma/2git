package toGit.migration.sources.dummy

import toGit.context.base.Context
import toGit.context.traits.HasSource

class DummySourceContext implements Context, HasSource {
    public DummySourceContext() {
        source = new DummySource()
    }
}
