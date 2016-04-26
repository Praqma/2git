package all2all.context.traits

import all2all.context.base.Context
import all2all.migration.plan.Criteria

trait HasCriteria implements Context {
    List<Criteria> criteria = []
}
