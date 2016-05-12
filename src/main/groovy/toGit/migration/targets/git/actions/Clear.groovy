package toGit.migration.targets.git.actions

import toGit.migration.MigrationManager
import toGit.migration.plan.Action

class Clear extends Action {
    File path

    public Clear(File path) {
        this.path = path
    }

    @Override
    void act(HashMap<String, Object> extractionMap) {
        new File(MigrationManager.instance.target.workspace).listFiles().findAll { !it.name.startsWith(".git") }.each {
            if (it.directory) it.deleteDir()
            else it.delete()
        }
    }
}
