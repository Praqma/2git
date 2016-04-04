package migration.targets.git.actions

import migration.plan.Action
import migration.targets.git.GitOptions
import migration.targets.git.GitUtil

class Setup extends Action {
    GitOptions options

    public Setup(GitOptions options) {
        this.options = options
    }

    @Override
    void act(HashMap<String, Object> extractionMap) {
        GitUtil.setUpRepository(options)
    }
}