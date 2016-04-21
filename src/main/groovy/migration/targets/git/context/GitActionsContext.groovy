package migration.targets.git.context

import context.base.Context
import groovy.util.logging.Slf4j
import migration.targets.git.GitOptions
import migration.targets.git.actions.Clear
import migration.targets.git.actions.Setup

@Slf4j
trait GitActionsContext implements Context {

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
