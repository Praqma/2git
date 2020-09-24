package togit.migration.sources.ccucm

import net.praqma.clearcase.ucm.view.SnapshotView
import org.slf4j.LoggerFactory

class CcucmOptions {

    final static LOG = LoggerFactory.getLogger(this.class)

    String stream
    String component
    String migrationProject
    boolean readOnlyMigrationStream
    SnapshotView.Components loadComponents = SnapshotView.Components.MODIFIABLE
    boolean cleanup = true
    boolean uniqueViews = false
    boolean uniqueStreams = false

    void setReadOnlyMigrationStream(boolean readOnlyMigrationStream) {
        this.readOnlyMigrationStream = readOnlyMigrationStream
        configCheck()
    }

    void setLoadComponents(SnapshotView.Components loadComponents) {
        this.loadComponents = loadComponents
        configCheck()
    }

    void configCheck() {
        if (readOnlyMigrationStream && loadComponents == SnapshotView.Components.MODIFIABLE) {
            LOG.warn('Migrating modifiable components in combination with read-only migration streams can cause unexpected behaviour')
        }
    }
}
