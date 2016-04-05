package migration.targets.git

import groovy.util.logging.Slf4j
import net.praqma.util.execute.AbnormalProcessTerminationException
import net.praqma.util.execute.CommandLine
import org.apache.commons.io.FileUtils

/**
 * A class that facilitates calling Git commands
 */
@Slf4j
class GitUtil {

    /**
     * Calls Git with given arguments in the set path.
     * @param args The arguments to call Git with.
     * @return the exit code of the command
     */
    static int call(File path, String... args) {
        try {
            callOrDie(path, args)
        } catch (AbnormalProcessTerminationException ex) {
            log.warn("Command exited with status code {}", ex.exitValue)
            return ex.exitValue
        }
        return 0
    }

    /**
     * Calls Git with given arguments in the set path.
     * Throws exceptions on error/failure
     * @param args the arguments to call Git with
     */
    static void callOrDie(File path, String... args) {
        String cmd = "git " + args.join(" ");
        log.info("Executing '{}' in {}", cmd, path)
        CommandLine.newInstance().run(cmd, path).stdoutBuffer.eachLine { line -> println line }
    }

    /**
     * Configures git in the set path using the passed in cc2git Stream
     * @param stream The Stream to use for configuration
     */
    static void configureRepository(File path, GitOptions options) {
        if (options.user) {
            callOrDie(path, "config", "user.name", options.user)
            log.trace("Set git user.name to $options.user.")
        }
        if (options.email) {
            callOrDie(path, "config", "user.email", options.email)
            log.trace("Set git user.email to $options.user.")
        }
        writeGitIgnore(options)
    }

    static void writeGitIgnore(File path, GitOptions options) {
        def gitIgnore = new File(path, '.gitignore');
        if (gitIgnore.exists()) FileUtils.forceDelete(gitIgnore)
        options.ignore.each { rule ->
            FileUtils.writeStringToFile(gitIgnore, rule + '\n', true)
        }
    }

    /**
     * Checkout a branch, if it doesn't exist, check out an orphan.
     * @param branch the branch to check out
     */
    static void forceCheckout(File path, String branch) {
        if (!call(path, "rev-parse --verify $branch"))
            callOrDie(path, "checkout $branch")
        else {
            callOrDie(path, "checkout --orphan $branch")
        }
    }

    /**
     * inits a migration Git repository
     * @param options the configuration of the git repo
     */
    static void initRepository(File path) {
        if (!path.exists()) {
            log.info("Git dir $path does not exist, performing first time setup.")
            FileUtils.forceMkdir(path)
            callOrDie(path, "init")
        }
    }
}
