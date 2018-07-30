package togit.migration.targets.dummy

import togit.context.TargetContext

class DummyTargetContext extends TargetContext {
    DummyTargetContext() {
        target = new DummyTarget()
    }
}
