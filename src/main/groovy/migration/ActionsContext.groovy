package migration

import git.Git
import groovy.text.SimpleTemplateEngine
import net.praqma.util.execute.CommandLine

class ActionsContext {
    List<Action> actions = []

    def void git(String command) {
        actions.add(new Action() {
            @Override
            void act(HashMap<String, Object> extractionMap) {
                def expandedCommand = new SimpleTemplateEngine().createTemplate(command).make(extractionMap).toString()
                Git.callOrDie(expandedCommand)
            }
        })
    }

    def void cmd(String command) {
        def scriptPath = new File(getClass().protectionDomain.codeSource.location.path).parent
        cmd(command, scriptPath)
    }

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
