package context.traits

import context.base.Context
import migration.plan.Criteria

trait HasCriteria implements Context {
    List<Criteria> criteria = []
}
