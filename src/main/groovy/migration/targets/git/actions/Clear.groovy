package migration.targets.git.actions

import migration.plan.Action
import migration.targets.git.GitUtil

class Clear extends Action {
    @Override
    void act(HashMap<String, Object> extractionMap) {
        GitUtil.path.listFiles().findAll { !it.name.startsWith(".git") }.each {
            if (it.directory) it.deleteDir()
            else it.delete()
        }
    }
}
