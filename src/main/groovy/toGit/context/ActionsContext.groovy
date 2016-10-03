package toGit.context

import groovy.text.SimpleTemplateEngine
import net.praqma.util.execute.CommandLine
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import toGit.context.base.Context
import toGit.context.traits.HasActions
import toGit.migration.MigrationManager
import toGit.migration.plan.Action
import toGit.utils.FileHelper

class ActionsContext implements Context, HasActions {

    final static log = LoggerFactory.getLogger(this.class)

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
        log.debug("Registering action - copy")
        actions.add(new Action() {
            @Override
            void act(HashMap<String, Object> extractionMap) {
                def sourceDir = new File(source)
                def targetDir = new File(target)
                sourceDir.listFiles().each { file ->
                    if (file.isDirectory())
                        FileUtils.copyDirectoryToDirectory(file, targetDir)
                    else
                        FileUtils.copyFileToDirectory(file, targetDir)
                }
            }
        })
        log.debug("Registered action - copy")
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
        log.debug("Registering action - move")
        actions.add(new Action() {
            @Override
            void act(HashMap<String, Object> extractionMap) {
                def sourceDir = new File(source)
                def targetDir = new File(target)
                sourceDir.listFiles().each { file ->
                    if (file.isDirectory())
                        FileUtils.moveDirectoryToDirectory(file, targetDir, true)
                    else
                        FileUtils.moveFileToDirectory(file, targetDir, true)
                }
            }
        })
        log.debug("Registered action - copy")
    }

    /**
     * Registers a command line action to execute
     * @param command the command to execute
     */
    // FIXME call cmd(string, string) below
    void cmd(String command) {
        log.debug("Registering action - cmd")
        actions.add(new Action() {
            @Override
            void act(HashMap<String, Object> extractionMap) {
                def expandedCommand = new SimpleTemplateEngine().createTemplate(command).make(extractionMap).toString()
                CommandLine.newInstance().run(expandedCommand).stdoutBuffer.eachLine { line ->
                    log.info(line)
                }
            }
        })
        log.debug("Registered action - cmd")
    }

    /**
     * Registers a command line action to execute
     * @param command the command to execute
     * @param path the path to execute the command in
     */
    void cmd(String command, String path) {
        log.debug("Registering action - cmd")
        actions.add(new Action() {
            @Override
            void act(HashMap<String, Object> extractionMap) {
                def expandedCommand = new SimpleTemplateEngine().createTemplate(command).make(extractionMap).toString()
                CommandLine.newInstance().run(expandedCommand, new File(path)).stdoutBuffer.eachLine { line ->
                    log.info(line)
                }
            }
        })
        log.debug("Registered action - cmd")
    }

    /**
     * Executes a custom groovy command
     * @param closure the closure to run
     */
    void custom(Closure closure) {
        log.debug("Registering action - custom")
        actions.add(new Action() {
            @Override
            void act(HashMap<String, Object> extractionMap) {
                closure.delegate = this
                closure.resolveStrategy = Closure.DELEGATE_FIRST
                closure.call(extractionMap)
            }
        })
        log.debug("Registered action - custom")
    }

    void emptyDir(String dir) {
        log.debug("Registering action - emptyDir")
        actions.add(new Action() {
            @Override
            void act(HashMap<String, Object> extractionMap) {
                FileHelper.emptyDirectory(new File(dir))
            }
        })
        log.debug("Registered action - emptyDir")
    }

    /**
     * Flattens the directory structure a given amount of times, emptying subdirectories into the root and deleting the empty subdirectories.
     * @param dir the directory to flatten
     * @param amount the amount of times to flatten the directory structure
     */
    void flattenDir(String dir, int amount) {
        log.debug("Registering action - flattenDir")
        actions.add(new Action() {
            @Override
            void act(HashMap<String, Object> extractionMap) {
                amount.times {
                    FileHelper.singleFlattenDirectory(new File(dir))
                }
            }
        })
        log.debug("Registered action - flattenDir")
    }

    /**
     * Runs missing methods as command line calls.
     * ex.: mkdir '/home/me/repository'
     * @param name the name of the method that was called
     * @param args the arguments the method was called with
     */
    void methodMissing(String name, Object args) {
        log.warn("Could not find action '$name' with arguments '$args', attempting to register as a plain command")
        def arguments = args.join()
        cmd("$name $arguments")
    }
}
