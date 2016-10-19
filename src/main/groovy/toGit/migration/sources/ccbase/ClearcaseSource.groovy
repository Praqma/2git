package toGit.migration.sources.ccbase

import org.slf4j.LoggerFactory
import toGit.context.base.Context
import toGit.migration.plan.Criteria
import toGit.migration.plan.Snapshot
import toGit.migration.sources.MigrationSource
import toGit.migration.sources.ccbase.context.ClearcaseCriteriaContext
import toGit.migration.sources.ccbase.context.ClearcaseExtractionsContext

class ClearcaseSource implements MigrationSource {

    final static log = LoggerFactory.getLogger(this.class)

    // Set in DSL
    String configSpec;
    String labelVob; // TODO Does this make sense to users? The vob used to get labels from the created view?

    // Set internally
    String viewTag;

    @Override
    List<Snapshot> getSnapshots(List<Criteria> initialFilter) {
        log.info("Retrieving labels from vob ${labelVob}")
        def output = runCommand(["cleartool", "lstype", "-kind", "lbtype", "-short", "-invob", labelVob])
        def labels = output.split("\n")
        return labels.collect{new ClearcaseSnapshot(it)}
    }

    @Override
    void checkout(Snapshot snapshot) {
        // Code to prepare a workspace for the given snapshot
        log.info('Done (?)')
    }

    @Override
    void prepare() {
        viewTag = "2git-${UUID.randomUUID()}"

        log.info("Creating snapshot view '${viewTag}'")
        runCommand(["cleartool", "mkview", "-snapshot", "-tag", viewTag, "-stgloc", "-auto", "${workspace}"])

        log.info("Setting config spec to $configSpec")
        runCommand(["cleartool", "setcs", configSpecAsFile().absolutePath])
    }

    /**
     * Runs given String list as a command, logging and returning the output
     * @param command String list representing the command to execute
     * @return The process output as a String
     */
    String runCommand(List<String> command) {
        def arguments = command.collect {it.toString()}
        def builder = new ProcessBuilder(arguments).redirectErrorStream(true).directory(new File(workspace))
        def process = builder.start()
        def output = ""
        process.in.eachLine {
            log.info(it)
            output += "\n$it";
        }
        log.info(process.waitFor().toString())
        return output
    }

    /**
     * Checks if the given config spec exists
     * @return The given config spec as a Java.io.File
     */
    File configSpecAsFile() {
        def configSpecFile = new File(configSpec);
        if(!configSpecFile.exists() || configSpecFile.isDirectory()) {
            log.error("Could not find config spec at ${configSpecFile.absolutePath}")
            System.exit(1)
        }
        return configSpecFile;
    }

    @Override
    void cleanup() {
    }

    @Override
    Context withCriteria(Context criteriaContext) {
        return criteriaContext as ClearcaseCriteriaContext
    }

    @Override
    Context withExtractions(Context extractionsContext) {
        return extractionsContext as ClearcaseExtractionsContext
    }
}
