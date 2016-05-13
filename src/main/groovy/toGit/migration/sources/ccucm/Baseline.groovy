package toGit.migration.sources.ccucm

import toGit.migration.plan.Snapshot
import net.praqma.clearcase.ucm.entities.Baseline as CoolBaseline

class Baseline extends Snapshot {
    CoolBaseline source

    Baseline(CoolBaseline source){
        super(source.fullyQualifiedName)
        this.source = source
    }
}
