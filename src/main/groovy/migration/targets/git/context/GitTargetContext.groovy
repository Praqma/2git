package migration.targets.git.context

import context.base.Context
import context.traits.HasTarget
import groovy.util.logging.Slf4j
import migration.targets.git.GitOptions
import migration.targets.git.GitTarget

@Slf4j
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
        log.info("Added $args to git ignore.")
    }

    /**
     * Sets the Git user
     * @param user the user name
     */
    void user(String user) {
        target.options.user = user
        log.info("Set user to $user.")
    }

    /**
     * Sets the Git user email
     * @param email the user email
     */
    void email(String email) {
        target.options.email = email
        log.info("Set email to $email.")
    }

    /**
     * Sets the Git path
     */
    void workspace(String path) {
        target.workspace = path
        log.info("Set workspace to $path.")
    }
}
