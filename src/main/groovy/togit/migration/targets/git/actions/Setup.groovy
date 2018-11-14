package togit.migration.targets.git.actions

import org.slf4j.LoggerFactory
import togit.migration.plan.Action
import togit.migration.targets.git.GitOptions
import togit.migration.targets.git.GitUtil

class Setup extends Action {

    final static LOG = LoggerFactory.getLogger(this.class)

    File path
    GitOptions options

    Setup(File path, GitOptions options) {
        this.path = path
        this.options = options
    }

    @Override
    void act(HashMap<String, Object> extractionMap) {
        LOG.info("Initializing Git repository: $path")
        GitUtil.initRepository(path)
        GitUtil.configureRepository(path, options)
        LOG.info('Initialized Git repository')
    }
}
