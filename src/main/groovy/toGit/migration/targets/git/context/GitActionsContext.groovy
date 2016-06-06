package toGit.migration.targets.git.context

import toGit.context.base.Context
import toGit.migration.targets.git.GitOptions
import toGit.migration.targets.git.actions.Clear
import toGit.migration.targets.git.actions.Setup
import org.slf4j.Logger
import org.slf4j.LoggerFactory

//@Log
trait GitActionsContext implements Context {
    final static Logger log = LoggerFactory.getLogger(GitActionsContext.class)

    /**
     * Deletes all but '.git*' files/dirs in the Git path
     */
    void clear(String path) {
        actions.add(new Clear(new File(path)))
        log.info("Registered 'clear' action.")
    }

    /**
     * Sets up a default repository using defined {@link GitOptions}
     * @param options the GitOptions to use
     */
    void setup(File path, GitOptions options) {
        actions.add(new Setup(path, options))
        log.info("Registered 'setup' action.")
    }
}
