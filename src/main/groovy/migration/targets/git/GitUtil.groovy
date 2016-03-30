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
    static File path = new File("./output") // The path the Git commands will be run in TODO Remove this

    /**
     * Calls Git with given arguments in the set path.
     * @param args The arguments to call Git with.
     * @return the exit code of the command
     */
    static int call(String... args) {
        try {
            callOrDie(args)
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
    static void callOrDie(String... args) {
        String cmd = "git " + args.join(" ");
        log.info("Executing '{}' in {}", cmd, path)
        CommandLine.newInstance().run(cmd, path).stdoutBuffer.eachLine { line -> println line }
    }

    /**
     * Configures git in the set path using the passed in cc2git Stream
     * @param stream The Stream to use for configuration
     */
    static void configureRepository(GitOptions options) {
        if (options.user) {
            callOrDie("config", "user.name", options.user)
            log.trace("Set git user.name to {}.", options.user)
        }
        if (options.email) {
            callOrDie("config", "user.email", options.email)
            log.trace("Set git user.email to {}.", options.email)
        }
        writeGitIgnore(options)
    }

    static void writeGitIgnore(GitOptions options) {
        def gitIgnore = new File(options.workTree, '.gitignore');
        if (gitIgnore.exists()) FileUtils.forceDelete(gitIgnore)
        options.ignore.each { rule ->
            FileUtils.writeStringToFile(gitIgnore, rule + '\n', true)
        }
    }

    /**
     * Checkout a branch, if it doesn't exist, check out an orphan.
     * @param branch the branch to check out
     */
    static void forceCheckout(String branch) {
        if (!call("rev-parse --verify " + branch))
            callOrDie("checkout " + branch)
        else {
            callOrDie("checkout --orphan " + branch)
        }
    }

    /**
     * Do the first time setup of a migration Git repository
     * @param options the configuration of the git repo
     */
    static void setUpRepository(GitOptions options) {
        File gitDir = new File(options.dir)
        File workTree = new File(options.workTree)
        path = workTree
        if (!workTree.exists()) FileUtils.forceMkdir(workTree)
        if (!gitDir.exists()) {
            log.info("Git dir {} does not exist, performing first time setup.", gitDir)
            FileUtils.forceMkdir(gitDir)
            callOrDie("init --separate-git-dir $options.dir")
        }
        configureRepository(options)
    }
}
