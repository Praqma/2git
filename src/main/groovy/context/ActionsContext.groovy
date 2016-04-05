package context

import context.base.Context
import context.traits.HasActions
import groovy.text.SimpleTemplateEngine
import migration.plan.Action
import net.praqma.util.execute.CommandLine
import utils.FileHelper

class ActionsContext implements Context, HasActions {

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
    }

    /**
     * Runs missing methods as command line calls.
     * ex.: mkdir '/home/me/repository'
     * @param name the name of the method that was called
     * @param args the arguments the method was called with
     */
    void methodMissing(String name, Object args) {
        cmd("$name $args")
    }

    /**
     * Flattens the directory structure a given amount of times, emptying subdirectories into the root and deleting the empty subdirectories.
     * @param dir the directory to flatten
     * @param amount the amount of times to flatten the directory structure
     */
    void flattenDir(String dir, int amount){
        actions.add(new Action() {
            @Override
            void act(HashMap<String, Object> extractionMap) {
                amount.times {
                    FileHelper.emptySubDirectories(new File(dir))
                }
            }
        })
    }
}
