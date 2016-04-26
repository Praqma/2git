package all2all.context.traits

import all2all.context.base.Context
import all2all.migration.plan.Action

trait HasActions implements Context {
    List<Action> actions = []
}
