package toGit.migration.targets.dummy

import toGit.context.base.Context
import toGit.context.traits.TargetContext

class DummyTargetContextContext implements Context, TargetContext {
    public DummyTargetContextContext() {
        target = new DummyTarget()
    }
}
