package toGit.migration.targets.dummy

import toGit.context.base.Context
import toGit.context.traits.HasTarget

class DummyTargetContext implements Context, HasTarget {
    public DummyTargetContext() {
        target = new DummyTarget()
    }

}
