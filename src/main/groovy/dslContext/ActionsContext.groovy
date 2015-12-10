package dslContext

import git.Git
import groovy.text.SimpleTemplateEngine
import migration.filter.actions.Action
import net.praqma.util.execute.CommandLine

class ActionsContext {
    List<Action> actions = []

    /**
     * Executes a Git command in the branch repository
     * @param command the Git command to execute
     */
    def void git(String command) {
        actions.add(new Action() {
            @Override
            void act(HashMap<String, Object> extractionMap) {
                def expandedCommand = new SimpleTemplateEngine().createTemplate(command).make(extractionMap).toString()
                Git.callOrDie(expandedCommand)
            }
        })
    }

    /**
     * Executes a CLI command in the script path
     * @param command the command to execute
     */
    def void cmd(String command) {
        def scriptPath = new File(getClass().protectionDomain.codeSource.location.path).parent
        cmd(command, scriptPath)
    }

    /**
     * Executes a CLI command in the given path
     * @param command the command to execute
     * @param path the path to execute the command in
     */
    def void cmd(String command, String path) {
        actions.add(new Action() {
            @Override
            void act(HashMap<String, Object> extractionMap) {
                def expandedCommand = new SimpleTemplateEngine().createTemplate(command).make(extractionMap).toString()
                CommandLine.newInstance().run(expandedCommand, new File(path)).stdoutBuffer.eachLine { line -> println line}
            }
        })
    }
}
