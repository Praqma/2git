package toGit.context.traits

import toGit.context.base.Context
import toGit.migration.plan.Extraction

trait HasExtractions implements Context {
    List<Extraction> extractions = []
}
