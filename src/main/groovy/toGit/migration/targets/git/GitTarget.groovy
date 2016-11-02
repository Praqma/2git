package toGit.migration.targets.git

import toGit.migration.MigrationManager
import toGit.migration.targets.MigrationTarget
import toGit.migration.targets.git.actions.Clear
import toGit.migration.targets.git.actions.FillEmptyDirs
import toGit.migration.targets.git.actions.Setup

class GitTarget implements MigrationTarget {
    GitOptions options

    @Override
    void prepare() {
        if (options.defaultSetup)
            MigrationManager.instance.plan.befores.add(new Setup(new File(workspace), options))
    }

    @Override
    void cleanup() {

    }

    /**
     * Deletes all but '.git*' files/dirs in the Git path
     */
    void clear() {
        addAction('clear', new Clear(workspace))
    }

    /**
     * Sets up a default repository using defined {@link GitOptions}
     * @param options the GitOptions to use
     */
    void setup(File path, GitOptions options) {
        addAction('setup', new Setup(path, options))
    }

    /**
     * Puts a dummy file in all the empty directories in the workspace.
     * This allows you to add 'empty' directories to the git repository.
     */
    void fillEmptyDirs() {
        addAction('fillEmptyDirs', new FillEmptyDirs(workspace))
    }
}
