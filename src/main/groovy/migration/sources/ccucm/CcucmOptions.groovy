package migration.sources.ccucm

import net.praqma.clearcase.ucm.view.SnapshotView as CoolSnapshotView

class CcucmOptions {
    String dir = new File("./output/source").absolutePath

    String stream
    String component
    String migrationProject

    CoolSnapshotView.Components loadComponents = CoolSnapshotView.Components.MODIFIABLE
    boolean readOnlyMigrationStream = false
}
