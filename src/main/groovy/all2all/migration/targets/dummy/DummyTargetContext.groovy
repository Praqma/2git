package all2all.migration.targets.dummy

import all2all.context.base.Context
import all2all.context.traits.HasTarget

class DummyTargetContext implements Context, HasTarget {
    public DummyTargetContext() {
        target = new DummyTarget()
    }

}
