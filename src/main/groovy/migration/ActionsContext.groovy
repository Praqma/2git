package migration

import git.Git
import groovy.text.SimpleTemplateEngine

class ActionsContext {
    List<Action> actions = []

    def void git(String cmd) {
        actions.add(new Action() {
            @Override
            void act(HashMap<String, Object> extractionMap) {
                def expandedCommand = new SimpleTemplateEngine().createTemplate(cmd).make(extractionMap).toString()
                Git.callOrDie(expandedCommand)
            }
        })
    }
}
