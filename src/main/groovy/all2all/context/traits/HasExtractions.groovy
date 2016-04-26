package all2all.context.traits

import all2all.context.base.Context
import all2all.migration.plan.Extraction

trait HasExtractions implements Context {
    List<Extraction> extractions = []
}
