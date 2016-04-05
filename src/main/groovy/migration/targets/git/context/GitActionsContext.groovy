package migration.targets.git.context

import context.traits.HasActions
import migration.targets.git.GitOptions
import migration.targets.git.actions.Clear
import migration.targets.git.actions.Setup

trait GitActionsContext implements HasActions {

    /**
     * Deletes all but '.git*' files/dirs in the Git path
     */
    void clear() {
        actions.add(new Clear())
    }

    /**
     * Sets up a default repository using defined {@link GitOptions}
     * @param options the GitOptions to use
     */
    void setup(File path, GitOptions options) {
        actions.add(new Setup(path, options))
    }
}
