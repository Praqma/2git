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

    // Set in DSL
    String configSpec;
    String labelVob;
    String viewTag;
    List<String> vobPaths;

    @Override
    List<Snapshot> getSnapshots(List<Criteria> initialFilter) {
        log.info("Retrieving labels from vob ${labelVob}")
        def output = runCommand(["cleartool", "lstype", "-kind", "lbtype", "-short", "-invob", labelVob], true, false)
        def labels = output.split("\n")
        return labels.collect{new ClearCaseSnapshot(it)}
    }

    @Override
    void checkout(Snapshot snapshot) {
        writeNewConfigSpec(snapshot.identifier)
        setConfigSpec()
        log.info('Done preparing snapshot ' + snapshot.identifier)
    }

    @Override
    void prepare() {
        log.info("Creating snapshot view '${viewTag}'")
        runCommand(["cleartool", "mkview", "-snapshot", "-tag", viewTag, "-stgloc", "-auto", "${workspace}"], false, true)

        log.info("Creating new basic config spec")
        writeNewConfigSpec(null)

        log.info("Setting config spec to $configSpec")
        setConfigSpec()
    }

    void writeNewConfigSpec(String label) {
        def csData = null
        if (label == null || label.isEmpty()) {
            csData =
                [ "element * CHECKEDOUT",
                  "element * /main/LATEST",
                  "" ]
        }
        else {
            csData =
                [ "element * CHECKEDOUT",
                  "element * $label",
                  "element * /main/LATEST",
                  "" ]
        }
        vobPaths.each {
            csData.add("load /$it")
        }

        configSpecAsFile().withWriter { out ->
            csData.each {
                out.println it
            }
        }
    }

    void setConfigSpec() {
        runCommand(["cleartool", "setcs", "-force", configSpecAsFile().absolutePath], true, true)
    }

    String runCommand(List<String> command) {
        return runCommand(command, true, true)
    }

    /**
     * Runs given String list as a command, logging and returning the output
     * @param command String list representing the command to execute
     * @return The process output as a String
     */
    String runCommand(List<String> command, boolean runInWorkspace, boolean printOutput) {
        def arguments = command.collect {it.toString()}
        def builder = new ProcessBuilder(arguments).redirectErrorStream(true)
        builder = runInWorkspace ? builder.directory(new File(workspace)) : builder
        def process = builder.start()
        def output = ""
        process.in.eachLine {
            if(printOutput) log.info(it)
            output += "\n$it";
        }
        log.info("runCommand return: ${process.waitFor()}")
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
}
