package toGit.context.traits

import toGit.context.base.Context
import toGit.migration.plan.Criteria

trait HasCriteria implements Context {
    List<Criteria> criteria = []
}
