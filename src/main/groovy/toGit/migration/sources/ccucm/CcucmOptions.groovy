package toGit.migration.sources.ccucm

import net.praqma.clearcase.ucm.view.SnapshotView as CoolSnapshotView

class CcucmOptions {
    String stream
    String component
    String migrationProject

    List<String> ignore = ["view.dat", "*.updt"]

    CoolSnapshotView.Components loadComponents = CoolSnapshotView.Components.MODIFIABLE
    boolean readOnlyMigrationStream
}
