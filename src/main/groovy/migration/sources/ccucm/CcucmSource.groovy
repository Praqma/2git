package migration.sources.ccucm

import context.CriteriaContext
import context.ExtractionsContext
import groovy.util.logging.Slf4j
import migration.plan.Criteria
import migration.sources.MigrationSource
import migration.sources.Snapshot
import migration.sources.ccucm.context.CcucmCriteriaContext
import migration.sources.ccucm.context.CcucmExtractionsContext
import net.praqma.clearcase.PVob as CoolVob
import net.praqma.clearcase.ucm.entities.Component as CoolComponent
import net.praqma.clearcase.ucm.entities.Project as CoolProject
import net.praqma.clearcase.ucm.entities.Stream as CoolStream
import net.praqma.clearcase.ucm.utils.BaselineFilter
import net.praqma.clearcase.ucm.view.SnapshotView as CoolSnapshotView
import utils.StringExtensions

@Slf4j
class CcucmSource implements MigrationSource {
    CoolSnapshotView migrationView
    CoolStream migrationStream

    CoolVob vob
    CoolComponent component
    CoolStream stream
    CoolStream parentStream

    CcucmOptions options

    @Override
    void prepare() {
        vob = Cool.getPVob(StringExtensions.parseClearCaseName(options.stream).vob)
        component = Cool.getComponent(options.component, vob)
        stream = Cool.getStream(options.stream, vob)
        if (options.migrationProject) {
            CoolProject targetProject = CoolProject.get(options.migrationProject, vob).load();
            parentStream = targetProject.integrationStream
        }
    }

    @Override
    void cleanup() {
        migrationView.remove()
        migrationStream.remove()
    }

    @Override
    CriteriaContext withCriteria(CriteriaContext criteriaContext) {
        return criteriaContext as CcucmCriteriaContext
    }

    @Override
    ExtractionsContext withExtractions(ExtractionsContext extractionsContext) {
        return extractionsContext as CcucmExtractionsContext
    }

    @Override
    List<Snapshot> getSnapshots(List<Criteria> criteria) {
        BaselineFilter baselineFilter = new AggregatedBaselineFilter(criteria)
        def baselines = Cool.getBaselines(component, stream, baselineFilter)
        log.info("Found {} baseline(s) matching given requirements: {}", baselines.size(), baselines)
        return baselines.collect { it ->
            new Baseline(it.fullyQualifiedName, it)
        }
    }

    @Override
    void checkout(Snapshot snapshot) {
        def baseline = ((Baseline) snapshot).source

        if (!migrationStream) { // Move this to setup? Too intense for setup?
            migrationStream = Cool.createStream(parentStream, baseline, component.shortname + "_cc2git_" + id, options.readOnlyMigrationStream)
            migrationView = Cool.createView(migrationStream, new File(options.dir), component.shortname + "_cc2git_" + id)
        }

        Cool.rebase(baseline, migrationView)
        Cool.updateView(migrationView, options.loadComponents)
    }
}
