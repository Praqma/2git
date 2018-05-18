package toGit.migration.sources.ccucm

import net.praqma.clearcase.ucm.entities.Baseline as CoolBaseline
import toGit.migration.plan.Snapshot

class Baseline extends Snapshot {
    CoolBaseline source

    Baseline(CoolBaseline source) {
        super(source.fullyQualifiedName)
        this.source = source
    }

    @Override
    String toString() {
        return "${source.shortname}@${source.PVob.name}".toString()
    }
}
