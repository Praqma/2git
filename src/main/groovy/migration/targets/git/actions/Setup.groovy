package migration.targets.git.actions

import migration.plan.Action
import migration.targets.git.GitOptions
import migration.targets.git.GitUtil

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