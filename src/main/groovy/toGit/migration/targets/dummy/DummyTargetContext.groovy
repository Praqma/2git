package toGit.migration.targets.dummy

import toGit.context.base.Context
import toGit.context.traits.TargetContext

class DummyTargetContext implements Context, TargetContext {
    public DummyTargetContext() {
        target = new DummyTarget()
    }
}
