package toGit.migration.targets.git.actions

import org.slf4j.LoggerFactory
import toGit.migration.plan.Action
import toGit.migration.targets.git.GitOptions
import toGit.migration.targets.git.GitUtil

class Setup extends Action {

    final static log = LoggerFactory.getLogger(this.class)

    File path
    GitOptions options

    public Setup(File path, GitOptions options) {
        this.path = path
        this.options = options
    }

    @Override
    void act(HashMap<String, Object> extractionMap) {
        log.info("Initializing Git repository in $path")
        GitUtil.initRepository(path)
        GitUtil.configureRepository(path, options)
        log.info("Initialized Git repository")
    }
}