package context.traits

import context.base.Context
import migration.plan.Extraction

trait HasExtractions implements Context {
    List<Extraction> extractions = []
}
