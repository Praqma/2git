package context.traits

import context.base.Context
import migration.plan.Action

trait HasActions implements Context {
    List<Action> actions = []
}
