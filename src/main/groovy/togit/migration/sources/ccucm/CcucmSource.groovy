package togit.migration.sources.ccucm

import net.praqma.clearcase.PVob as CoolVob
import net.praqma.clearcase.ucm.entities.Component as CoolComponent
import net.praqma.clearcase.ucm.entities.Baseline as CoolBaseline
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

    List<CoolStream> migrationStreams
    List<CoolSnapshotView> migrationViews

    CoolVob streamVob
    CoolVob componentVob
    CoolComponent component
    CoolStream stream
    CoolStream parentStream

    CcucmOptions options

    @Override
    void prepare() {
        String componentName = CcucmStringHelper.parseName(options.component).tag
        String componentVobName = CcucmStringHelper.parseName(options.component).vob
        componentVob = Cool.findPVob(componentVobName)
        component = Cool.findComponent(componentName, componentVob)

        String streamName = CcucmStringHelper.parseName(options.stream).tag
        String streamVobName = CcucmStringHelper.parseName(options.stream).vob
        streamVob = Cool.findPVob(streamVobName)
        stream = Cool.findStream(streamName, streamVob)

        if (options.migrationProject) {
            CoolProject targetProject = CoolProject.get(options.migrationProject, streamVob).load()
            parentStream = targetProject.integrationStream
        } else {
            parentStream = stream
        }

        migrationStreams = []
        migrationViews = []
    }

    @Override
    void cleanup() {
        if (!options.cleanup) {
            LOG.info("Source cleanup set to false, skipping cleanup")
            return
        }

        LOG.info('Cleaning up migration views')
        migrationViews.each { view ->
            view.remove()
            new File(view.path).deleteDir()
        }
        LOG.info('Cleaned up migration views')

        LOG.info('Cleaning up migration streams')
        migrationStreams.each { stream ->
            stream.remove()
        }
        LOG.info('Cleaned up migration streams')
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
        UUID baselineId = UUID.randomUUID()

        CoolBaseline baseline = ((Baseline) snapshot).source
        CoolStream migrationStream
        CoolSnapshotView migrationView

        // Determine the migration stream
        if (options.uniqueStreams || migrationStreams.isEmpty()) {
            migrationStream = Cool.spawnChildStream(parentStream, baseline, "2git-$baseline.shortname-$baselineId", options.readOnlyMigrationStream)
            migrationStreams.add(migrationStream)
        } else {
            migrationStream = migrationStreams.last()
        }

        // Determine the migration view
        if (options.uniqueViews || migrationViews.isEmpty()) {
            File targetLocation = options.uniqueViews ? new File(workspace, baseline.shortname) : new File(workspace)
            migrationView = Cool.spawnView(migrationStream, targetLocation, "2git-$baseline.shortname-$baselineId")
            migrationViews.add(migrationView)
        } else {
            migrationView = migrationViews.last()
        }

        Cool.rebase(baseline, migrationView)
        Cool.updateView(migrationView, options.loadComponents)
    }
}
