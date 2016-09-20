package toGit.migration.targets.git.actions

import toGit.migration.plan.Action

class Clear extends Action {
    String path

    public Clear(String path) {
        this.path = path
    }

    @Override
    void act(HashMap<String, Object> extractionMap) {
        new File(path).listFiles().findAll { !it.name.startsWith(".git") }.each {
            if (it.directory) it.deleteDir()
            else it.delete()
        }
    }
}
