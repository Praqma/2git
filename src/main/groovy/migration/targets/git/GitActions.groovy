package migration.targets.git

import groovy.text.SimpleTemplateEngine
import migration.plan.Action

class GitActions {

    class Clear extends Action{
        @Override
        void act(HashMap<String, Object> extractionMap) {
            GitUtil.path.listFiles().findAll { !it.name.startsWith(".git") }.each {
                if (it.directory) it.deleteDir()
                else it.delete()
            }
        }
    }

    class Git extends Action {
        String command

        Git(String command){
            this.command = command
        }

        @Override
        void act(HashMap<String, Object> extractionMap) {
            def expandedCommand = new SimpleTemplateEngine().createTemplate(command).make(extractionMap).toString()
            GitUtil.callOrDie(expandedCommand)
        }
    }

    class Setup extends Action {
        GitOptions options

        public Setup(GitOptions options){
            this.options = options
        }

        @Override
        void act(HashMap<String, Object> extractionMap) {
            GitUtil.setUpRepository(options)
        }
    }
}
