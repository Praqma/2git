package toGit.migration.sources.ccucm

import toGit.context.base.Context
import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot
import toGit.migration.sources.MigrationSource
import toGit.migration.sources.ccucm.context.CcucmCriteriaContext
import toGit.migration.sources.ccucm.context.CcucmExtractionsContext
import toGit.utils.FileHelper
import toGit.utils.StringExtensions
import groovy.util.logging.Slf4j
import net.praqma.clearcase.PVob as CoolVob
import net.praqma.clearcase.ucm.entities.Component as CoolComponent
import net.praqma.clearcase.ucm.entities.Project as CoolProject
import net.praqma.clearcase.ucm.entities.Stream as CoolStream
import net.praqma.clearcase.ucm.utils.BaselineFilter
import net.praqma.clearcase.ucm.view.SnapshotView as CoolSnapshotView

@Slf4j
class CcucmSource implements MigrationSource {
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
        def vobName = StringExtensions.parseClearCaseName(options.stream).vob
        def componentName = StringExtensions.parseClearCaseName(options.component).tag
        def streamName = StringExtensions.parseClearCaseName(options.stream).tag

        vob = Cool.getPVob(vobName)
        component = Cool.getComponent(componentName, vob)
        stream = Cool.getStream(streamName, vob)

        if (options.migrationProject) {
            CoolProject targetProject = CoolProject.get(options.migrationProject, vob).load()
            parentStream = targetProject.integrationStream
        } else {
            parentStream = stream
        }
    }

    @Override
    void cleanup() {
        if (migrationView) migrationView.remove()
        if (migrationStream) migrationStream.remove()
    }

    @Override
    Context withCriteria(Context criteriaContext) {
        return criteriaContext as CcucmCriteriaContext
    }

    @Override
    Context withExtractions(Context extractionsContext) {
        return extractionsContext as CcucmExtractionsContext
    }

    @Override
    List<Snapshot> getSnapshots(List<Criteria> criteria) {
        BaselineFilter baselineFilter = new AggregatedBaselineFilter(criteria)
        def baselines = Cool.getBaselines(component, stream, baselineFilter)
        log.info("Found {} baseline(s) matching given requirements: {}", baselines.size(), baselines)
        return baselines.collect { bl ->
            new Baseline(bl)
        }
    }

    @Override
    void checkout(Snapshot snapshot) {
        def baseline = ((Baseline) snapshot).source

        //TODO Move this to setup? Too intense for setup?
        if (!migrationStream)
            migrationStream = Cool.createStream(parentStream, baseline, "$component.shortname-2git-$id", options.readOnlyMigrationStream)
        if (!migrationView)
            migrationView = Cool.createView(migrationStream, new File(workspace), "$component.shortname-2git-$id")

        Cool.rebase(baseline, migrationView)
        FileHelper.emptyDirectory(migrationView.viewRoot)
        Cool.updateView(migrationView, options.loadComponents)
    }
}
