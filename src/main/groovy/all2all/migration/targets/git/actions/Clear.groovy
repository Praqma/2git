package all2all.migration.targets.git.actions

import all2all.migration.MigrationManager
import all2all.migration.plan.Action

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
