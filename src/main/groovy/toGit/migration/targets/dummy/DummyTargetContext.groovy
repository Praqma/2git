package toGit.migration.targets.dummy

import toGit.context.TargetContext

class DummyTargetContext extends TargetContext {
    public DummyTargetContext() {
        target = new DummyTarget()
    }
}
