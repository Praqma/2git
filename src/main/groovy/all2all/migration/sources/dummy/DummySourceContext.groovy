package all2all.migration.sources.dummy

import all2all.context.base.Context
import all2all.context.traits.HasSource

class DummySourceContext implements Context, HasSource {
    public DummySourceContext() {
        source = new DummySource()
    }
}
