package toGit.context.traits

import toGit.context.base.Context
import toGit.migration.plan.Action

trait HasActions implements Context {
    List<Action> actions = []
}
