package togit.migration.sources.dummy

import togit.context.SourceContext

class DummySourceContext extends SourceContext {
    DummySourceContext() {
        source = new DummySource()
    }
}
