package all2all.context

import all2all.context.base.Context
import all2all.context.traits.HasActions
import all2all.migration.MigrationManager
import all2all.migration.plan.Action
import all2all.utils.FileHelper
import groovy.text.SimpleTemplateEngine
import groovy.util.logging.Slf4j
import net.praqma.util.execute.CommandLine
import org.apache.commons.io.FileUtils

@Slf4j
class ActionsContext implements Context, HasActions {

    /**
     * Copies the contents of the source directory to the target directory.
     */
    void copy() {
        actions.add(new Action() {
            @Override
            void act(HashMap<String, Object> extractionMap) {
                def sourceDir = new File(MigrationManager.instance.source.workspace)
                def targetDir = new File(MigrationManager.instance.target.workspace)
                sourceDir.listFiles().each { file ->
                    if (file.isDirectory())
                        FileUtils.copyDirectoryToDirectory(file, targetDir)
                    else
                        FileUtils.copyFileToDirectory(file, targetDir)
                }
            }
        })
        log.info("Registered 'copy' action.")
    }

    /**
     * Moves the contents of the source directory to the target directory.
     */
    void move() {
        actions.add(new Action() {
            @Override
            void act(HashMap<String, Object> extractionMap) {
                def sourceDir = new File(MigrationManager.instance.source.workspace)
                def targetDir = new File(MigrationManager.instance.target.workspace)
                sourceDir.listFiles().each { file ->
                    if (file.isDirectory())
                        FileUtils.moveDirectoryToDirectory(file, targetDir, true)
                    else
                        FileUtils.moveFileToDirectory(file, targetDir, true)
                }
            }
        })
        log.info("Registered 'move' action.")
    }

    /**
     * Registers a command line action to execute
     * @param command the command to execute
     */
    void cmd(String command) {
        actions.add(new Action() {
            @Override
            void act(HashMap<String, Object> extractionMap) {
                def expandedCommand = new SimpleTemplateEngine().createTemplate(command).make(extractionMap).toString()
                CommandLine.newInstance().run(expandedCommand).stdoutBuffer.eachLine { line -> println line }
            }
        })
        log.info("Registered 'cmd' action.")
    }

    /**
     * Registers a command line action to execute
     * @param command the command to execute
     * @param path the path to execute the command in
     */
    void cmd(String command, String path) {
        actions.add(new Action() {
            @Override
            void act(HashMap<String, Object> extractionMap) {
                def expandedCommand = new SimpleTemplateEngine().createTemplate(command).make(extractionMap).toString()
                CommandLine.newInstance().run(expandedCommand, new File(path)).stdoutBuffer.eachLine { line -> println line }
            }
        })
        log.info("Registered 'cmd' action.")
    }

    /**
     * Executes a custom groovy command
     * @param closure the closure to run
     */
    void custom(Closure closure) {
        actions.add(new Action() {
            @Override
            void act(HashMap<String, Object> extractionMap) {
                closure.delegate = this
                closure.resolveStrategy = Closure.DELEGATE_FIRST
                closure.call(extractionMap)
            }
        })
        log.info("Registered 'custom' action.")
    }

    /**
     * Runs missing methods as command line calls.
     * ex.: mkdir '/home/me/repository'
     * @param name the name of the method that was called
     * @param args the arguments the method was called with
     */
    void methodMissing(String name, Object args) {
        log.info("Method '$name' not found. Attempting to register as a 'cmd' action.")
        def arguments = args.join()
        cmd("$name $arguments")
    }

    /**
     * Flattens the directory structure a given amount of times, emptying subdirectories into the root and deleting the empty subdirectories.
     * @param dir the directory to flatten
     * @param amount the amount of times to flatten the directory structure
     */
    void flattenDir(String dir, int amount) {
        actions.add(new Action() {
            @Override
            void act(HashMap<String, Object> extractionMap) {
                amount.times {
                    FileHelper.emptySubDirectories(new File(dir))
                }
            }
        })
        log.info("Registered 'flattenDir' action.")
    }
}
