package togit.context

import groovy.text.SimpleTemplateEngine
import net.praqma.util.execute.CommandLine
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import togit.migration.MigrationManager
import togit.migration.plan.Action
import togit.utils.FileHelper

class ActionsContext implements Context {

    final static LOG = LoggerFactory.getLogger(this.class)

    final List<Action> actions = []

    /**
     * Copies the contents of the source directory to the default target directory.
     */
    void copy() {
        copy(MigrationManager.instance.source.workspace, MigrationManager.instance.targets.values()[0].workspace)
    }

    /**
     * Copies the contents of the source directory to the target directory.
     */
    void copy(String source, String target) {
        LOG.debug('Registering action - copy')
        actions.add(new Action() {
            @Override
            void act(HashMap<String, Object> extractionMap) {
                def expandedSource = new SimpleTemplateEngine().createTemplate(source).make(extractionMap).toString()
                def expandedTarget = new SimpleTemplateEngine().createTemplate(target).make(extractionMap).toString()
                def sourceDir = new File(expandedSource)
                if (! sourceDir.exists() ) throw new Exception ("ERROR: Source directory:" + sourceDir + " does not exist")
                def targetDir = new File(expandedTarget)
                if (! targetDir.exists() ) throw new Exception ("ERROR: Target directory:" + targetDir + " does not exist")
                log.info ("Copy from: $expandedSource")
                log.info ("To - perserving dates: $expandedTarget")
                sourceDir.listFiles().each { file ->
                    if (file.isDirectory()) {
                        FileUtils.copyDirectoryToDirectory(file, targetDir)
                    else
                        FileUtils.copyFileToDirectory(file, targetDir, true )
                }
            }
        })
        LOG.debug('Registered action - copy')
    }

    /**
     * Moves the contents of the source directory to the target directory.
     */
    void move() {
        move(MigrationManager.instance.source.workspace, MigrationManager.instance.targets.values()[0].workspace)
    }

    /**
     * Moves the contents of the source directory to the target directory.
     */
    void move(String source, String target) {
        LOG.debug('Registering action - move')
        actions.add(new Action() {
            @Override
            void act(HashMap<String, Object> extractionMap) {
                File sourceDir = new File(source)
                File targetDir = new File(target)
                sourceDir.listFiles().each { file ->
                    if (file.isDirectory()) {
                        FileUtils.moveDirectoryToDirectory(file, targetDir, true)
                    } else {
                        FileUtils.moveFileToDirectory(file, targetDir, true)
                    }
                }
            }
        })
        LOG.debug('Registered action - move')
    }

    /**
     * Registers a command line action to execute
     * @param command the command to execute
     */
    void cmd(String command) {
        cmd(command, null)
    }

    /**
     * Registers a command line action to execute
     * @param command the command to execute
     * @param path the path to execute the command in
     */
    void cmd(String command, String path) {
        LOG.debug('Registering action - cmd')
        actions.add(new Action() {
            @Override
            void act(HashMap<String, Object> extractionMap) {
                def expandedCommand = new SimpleTemplateEngine().createTemplate(command).make(extractionMap).toString()
                log.info(expandedCommand)
                CommandLine.newInstance().run(expandedCommand, path ? new File(path) : null).stdoutBuffer.eachLine { line ->
                    log.info(line)
                }
            }
        })
        LOG.debug('Registered action - cmd')
    }

    /**
     * Executes a custom groovy command
     * @param closure the closure to run
     */
    void custom(Closure closure) {
        LOG.debug('Registering action - custom')
        actions.add(new Action() {
            @Override
            void act(HashMap<String, Object> extractionMap) {
                closure.delegate = this
                closure.resolveStrategy = Closure.DELEGATE_FIRST
                closure.call(extractionMap)
            }
        })
        LOG.debug('Registered action - custom')
    }

    void emptyDir(String dir) {
        LOG.debug('Registering action - emptyDir')
        actions.add(new Action() {
            @Override
            void act(HashMap<String, Object> extractionMap) {
                FileHelper.emptyDirectory(new File(dir))
            }
        })
        LOG.debug('Registered action - emptyDir')
    }

    /**
     * Flattens the directory structure a given amount of times,
     * emptying subdirectories into the root and deleting the empty subdirectories.
     * @param dir the directory to flatten
     * @param amount the amount of times to flatten the directory structure
     */
    void flattenDir(String dir, int amount) {
        LOG.debug('Registering action - flattenDir')
        actions.add(new Action() {
            @Override
            void act(HashMap<String, Object> extractionMap) {
                amount.times {
                    FileHelper.singleFlattenDirectory(new File(dir))
                }
            }
        })
        LOG.debug('Registered action - flattenDir')
    }

    /**
     * Runs missing methods as command line calls.
     * ex.: mkdir '/home/me/repository'
     * @param name the name of the method that was called
     * @param args the arguments the method was called with
     */
    void methodMissing(String name, Object args) {
        LOG.warn("Could not find action '$name' with arguments '$args', attempting to register as a plain command")
        String arguments = args.join()
        cmd("$name $arguments")
    }
}
