package toGit.migration.sources.ccucm

import net.praqma.clearcase.PVob as CoolVob
import net.praqma.clearcase.ucm.entities.Component as CoolComponent
import net.praqma.clearcase.ucm.entities.Project as CoolProject
import net.praqma.clearcase.ucm.entities.Stream as CoolStream
import net.praqma.clearcase.ucm.utils.BaselineFilter
import net.praqma.clearcase.ucm.view.SnapshotView as CoolSnapshotView
import org.apache.commons.io.filefilter.WildcardFileFilter
import org.slf4j.LoggerFactory
import toGit.context.base.Context
import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot
import toGit.migration.sources.MigrationSource
import toGit.migration.sources.ccucm.context.CcucmCriteriaContext
import toGit.migration.sources.ccucm.context.CcucmExtractionsContext
import toGit.utils.StringExtensions

class CcucmSource implements MigrationSource {

    final static log = LoggerFactory.getLogger(this.class)

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
        if (migrationView) {
            log.info("Cleaning up migration view")
            migrationView.remove()
            new File(migrationView.path).deleteDir()
            log.info("Cleaned up migration view")
        }
        if (migrationStream) {
            log.info("Cleaning up migration stream")
            migrationStream.remove()
            log.info("Cleaned up migration stream")
        }
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
        def baselineCount = baselines.size()
        log.info("Found $baselineCount baseline(s) matching given requirements: $baselines")
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
        Cool.updateView(migrationView, options.loadComponents)
        removeIgnoredFiles()
    }

    /**
     * Deletes files from the View matching the filters specified in the option's 'ignore'
     */
    void removeIgnoredFiles() {
        log.info("Deleting ignored files")
        def view = new File(migrationView.path)
        def filter = new WildcardFileFilter(options.ignore)
        view.listFiles(filter as FilenameFilter).each {
            it.delete()
        }
        log.info("Deleted ignored files")
    }
}
