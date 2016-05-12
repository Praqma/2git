package toGit.migration.targets.git.actions

import toGit.migration.plan.Action
import toGit.migration.targets.git.GitOptions
import toGit.migration.targets.git.GitUtil

class Setup extends Action {
    File path
    GitOptions options

    public Setup(File path, GitOptions options) {
        this.path = path
        this.options = options
    }

    @Override
    void act(HashMap<String, Object> extractionMap) {
        GitUtil.initRepository(path)
        GitUtil.configureRepository(path, options)
    }
}