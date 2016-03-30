package migration.targets.git.context

import context.traits.HasActions
import migration.targets.git.GitActions
import migration.targets.git.GitOptions

trait GitActionsContext implements HasActions {

    /**
     * Deletes all but '.git*' files/dirs in the Git path
     */
    void clear() {
        actions.add(new GitActions.Clear())
    }

    /**
     * Executes a Git command in the branch repository
     * @param command the Git command to execute
     */
    void git(String command) {
        actions.add(new GitActions.Git(command))
    }

    /**
     * Sets up a default repository using defined {@link GitOptions}
     * @param options the GitOptions to use
     */
    void setup(GitOptions options) {
        actions.add(new GitActions.Setup(options))
    }
}
