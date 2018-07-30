package togit.migration.sources.ccucm

import net.praqma.clearcase.ucm.entities.Baseline as CoolBaseline
import togit.migration.plan.Snapshot

class Baseline extends Snapshot {
    CoolBaseline source

    Baseline(CoolBaseline source) {
        super(source.fullyQualifiedName)
        this.source = source
    }

    @Override
    String toString() {
        "${source.shortname}@${source.PVob.name}".toString()
    }
}
