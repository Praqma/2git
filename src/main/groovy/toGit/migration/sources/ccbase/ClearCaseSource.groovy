package toGit.migration.sources.ccbase

import org.slf4j.LoggerFactory
import toGit.context.base.Context
import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot
import toGit.migration.sources.MigrationSource
import toGit.migration.sources.ccbase.context.ClearCaseCriteriaContext
import toGit.migration.sources.ccbase.context.ClearCaseExtractionsContext

class ClearCaseSource implements MigrationSource {

    final static log = LoggerFactory.getLogger(this.class)

    String configSpec;
    String labelVob;
    String viewTag;
    List<String> vobPaths = [];

    @Override
    List<Snapshot> getSnapshots(List<Criteria> initialFilter) {
        log.info("Retrieving labels from vob ${labelVob}")
        def labels = runCommand(["cleartool", "lstype", "-kind", "lbtype", "-short", "-invob", labelVob], true, false).split("\n")
        return labels.collect { new ClearCaseSnapshot(it) }
    }

    @Override
    void checkout(Snapshot snapshot) {
        updateConfigSpec(snapshot.identifier)
        setConfigSpec()
        log.info('Done preparing snapshot ' + snapshot.identifier)
    }

    @Override
    void prepare() {
        log.info("Creating snapshot view '${viewTag}'")
        runCommand(["cleartool", "mkview", "-snapshot", "-tag", viewTag, "-stgloc", "-auto", "${workspace}"], false, true)
    }

    @Override
    void cleanup() {
        runCommand(['cleartool', 'rmview', workspace], false, true)
    }

    @Override
    Context withCriteria(Context criteriaContext) {
        return criteriaContext as ClearCaseCriteriaContext
    }

    @Override
    Context withExtractions(Context extractionsContext) {
        return extractionsContext as ClearCaseExtractionsContext
    }

    /**
     * Updates the config spec to load the given label
     * @param label The label to update the spec with
     */
    private void updateConfigSpec(String label) {
        def spec = configSpecAsFile()
        if (label == null || label.isEmpty())
            spec.text = "element * CHECKEDOUT\nelement * /main/LATEST\n"
        else
            spec.text = "element * $label\n"

        vobPaths.each {
            spec.text += ("load /$it\n")
        }
        println(spec.text)
    }

    /**
     * Sets the view's config spec to
     */
    private void setConfigSpec() {
        runCommand(["cleartool", "setcs", "-force", configSpecAsFile().absolutePath], true, true)
    }

    /**
     * Runs given String list as a command, logging and returning the output
     * @param command String list representing the command to execute
     * @return The process output as a String
     */
    private String runCommand(List<String> command, boolean runInWorkspace, boolean printOutput) {
        def builder = new ProcessBuilder(command).redirectErrorStream(true)
        builder = runInWorkspace ? builder.directory(new File(workspace)) : builder
        def process = builder.start()
        if (printOutput) { process.in.eachLine { log.info(it) } }
        log.info("runCommand return: ${process.waitFor()}")
        return process.text
    }

    /**
     * Checks if the given config spec exists
     * @return The given config spec as a Java.io.File
     */
    private File configSpecAsFile() {
        def configSpecFile = new File(configSpec);
        if (!configSpecFile.exists() || configSpecFile.isDirectory()) {
            log.error("Could not find config spec at ${configSpecFile.absolutePath}")
            System.exit(1)
        }
        return configSpecFile;
    }
}
