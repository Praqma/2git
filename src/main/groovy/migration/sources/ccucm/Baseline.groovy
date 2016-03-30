package migration.sources.ccucm

import migration.sources.Snapshot
import net.praqma.clearcase.ucm.entities.Baseline as CoolBaseline

class Baseline extends Snapshot {
    CoolBaseline source

    Baseline(String identifier) {
        super(identifier)
    }

    Baseline(String identifier, CoolBaseline source) {
        this(identifier)
        this.source = source
    }
}
