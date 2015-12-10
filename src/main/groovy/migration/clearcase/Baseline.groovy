package migration.clearcase

import migration.filter.actions.Action
import migration.filter.extractions.Extraction
import net.praqma.clearcase.ucm.entities.Baseline as CoolBaseline

class Baseline {
    CoolBaseline source
    List<Action> actions = []
    List<Extraction> extractions = []
}
