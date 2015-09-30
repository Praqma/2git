package migration

import net.praqma.clearcase.ucm.entities.Baseline as CoolBaseline

class Baseline {
    CoolBaseline source
    List<Action> actions = []
    List<Extraction> extractions = []
}
