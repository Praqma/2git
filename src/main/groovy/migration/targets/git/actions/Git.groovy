package migration.targets.git.actions

import groovy.text.SimpleTemplateEngine
import migration.plan.Action
import migration.targets.git.GitUtil

class Git extends Action {
    String command

    Git(String command) {
        this.command = command
    }

    @Override
    void act(HashMap<String, Object> extractionMap) {
        def expandedCommand = new SimpleTemplateEngine().createTemplate(command).make(extractionMap).toString()
        GitUtil.callOrDie(expandedCommand)
    }
}
