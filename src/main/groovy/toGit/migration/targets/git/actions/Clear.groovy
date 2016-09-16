package toGit.migration.targets.git.actions

import toGit.migration.plan.Action

class Clear extends Action {
    File path

    public Clear(File path) {
        this.path = path
    }

    @Override
    void act(HashMap<String, Object> extractionMap) {
        path.listFiles().findAll { !it.name.startsWith(".git") }.each {
            if (it.directory) it.deleteDir()
            else it.delete()
        }
    }
}
