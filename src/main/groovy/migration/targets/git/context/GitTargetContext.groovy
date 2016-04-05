package migration.targets.git.context

import context.base.Context
import context.traits.HasTarget
import migration.targets.git.GitOptions
import migration.targets.git.GitTarget

class GitTargetContext implements Context, HasTarget {

    /**
     * GitOptionsContext constructor
     */
    public GitTargetContext() {
        target = new GitTarget(options: new GitOptions())
    }

    /**
     * Adds given String arguments to the Git ignore file
     * @param args the String arguments to add
     */
    void ignore(String... args) {
        target.options.ignore.addAll(args)
    }

    /**
     * Sets the Git user
     * @param user the user name
     */
    void user(String user) {
        target.options.user = user
    }

    /**
     * Sets the Git user email
     * @param email the user email
     */
    void email(String email) {
        target.options.email = email
    }

    /**
     * Sets the Git path
     */
    void dir(String path) {
        target.dir = path
    }
}
