package togit.migration.sources.ccucm

import net.praqma.clearcase.PVob as CoolVob
import net.praqma.clearcase.ucm.entities.Component as CoolComponent
import net.praqma.clearcase.ucm.entities.Project as CoolProject
import net.praqma.clearcase.ucm.entities.Stream as CoolStream
import net.praqma.clearcase.ucm.utils.BaselineList
import net.praqma.clearcase.ucm.view.SnapshotView as CoolSnapshotView
import org.slf4j.LoggerFactory
import togit.context.CriteriaContext
import togit.context.ExtractionsContext
import togit.migration.plan.Criteria
import togit.migration.plan.Snapshot
import togit.migration.sources.MigrationSource
import togit.migration.sources.ccucm.context.CcucmCriteriaContext
import togit.migration.sources.ccucm.context.CcucmExtractionsContext

class CcucmSource implements MigrationSource {

    final static LOG = LoggerFactory.getLogger(this.class)

    UUID id = UUID.randomUUID()
    CoolSnapshotView migrationView
    CoolStream migrationStream

    CoolVob vob
    CoolComponent component
    CoolStream stream
    CoolStream parentStream

    CcucmOptions options

    @Override
    void prepare() {
        String vobName = CcucmStringHelper.parseName(options.stream).vob
        String componentName = CcucmStringHelper.parseName(options.component).tag
        String streamName = CcucmStringHelper.parseName(options.stream).tag

        vob = Cool.findPVob(vobName)
        component = Cool.findComponent(componentName, vob)
        stream = Cool.findStream(streamName, vob)

        if (options.migrationProject) {
            CoolProject targetProject = CoolProject.get(options.migrationProject, vob).load()
            parentStream = targetProject.integrationStream
        } else {
            parentStream = stream
        }
    }

    @Override
    void cleanup() {
        if (migrationView) {
            LOG.info('Cleaning up migration view')
            migrationView.remove()
            new File(migrationView.path).deleteDir()
            LOG.info('Cleaned up migration view')
        }

        if (migrationStream) {
            LOG.info('Cleaning up migration stream')
            migrationStream.remove()
            LOG.info('Cleaned up migration stream')
        }
    }

    @Override
    void mixinCriteria() {
        CriteriaContext.mixin(CcucmCriteriaContext)
    }

    @Override
    void mixinExtractions() {
        ExtractionsContext.mixin(CcucmExtractionsContext)
    }

    @Override
    List<Snapshot> getSnapshots(List<Criteria> criteria) {
        BaselineList baselines = Cool.findBaselines(component, stream, new AggregatedBaselineFilter(criteria))
        LOG.info("Found ${baselines.size()} baseline(s) matching given requirements: ${baselines}")
        baselines.collect { bl -> new Baseline(bl) }
    }

    @Override
    void checkout(Snapshot snapshot) {
        Baseline baseline = ((Baseline) snapshot).source

        migrationStream = migrationStream ?: Cool.spawnChildStream(parentStream, baseline, "$component.shortname-2git-$id", options.readOnlyMigrationStream)
        migrationView = migrationView ?: Cool.spawnView(migrationStream, new File(workspace), "$component.shortname-2git-$id")

        Cool.rebase(baseline, migrationView)
        Cool.updateView(migrationView, options.loadComponents)
    }
}
