package all2all.migration.targets.git.actions

import all2all.migration.plan.Action
import all2all.migration.targets.git.GitOptions
import all2all.migration.targets.git.GitUtil

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