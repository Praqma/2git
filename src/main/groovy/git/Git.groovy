package git

@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
import migration.GitOptions
import net.praqma.util.execute.AbnormalProcessTerminationException
import net.praqma.util.execute.CommandLine
import org.apache.commons.io.FileUtils

/**
 * A class that facilitates calling Git commands
 */
@Slf4j
class Git {
    static String path = "./output" // The path the Git commands will be run in

    /**
     * Calls Git with given arguments in the set path.
     * @param args The arguments to call Git with.
     * @return the exit code of the command
     */
    static int call(String... args) {
        log.debug("Entering call().")
        try {
            callOrDie(args)
        } catch (AbnormalProcessTerminationException ex) {
            log.warn("Command exited with status code {}", ex.exitValue)
            return ex.exitValue
        }
        log.debug("Exiting call().")
        return 0
    }

    /**
     * Calls Git with given arguments in the set path.
     * Throws exceptions on error/failure
     * @param args the arguments to call Git with
     */
    static void callOrDie(String... args) {
        String cmd = "git " + args.join(" ");
        log.info("Running '{}' in {}", cmd, path)
        log.info("Executing: {}", cmd)
        CommandLine.newInstance().run(cmd, new File(path)).stdoutBuffer.eachLine { line -> println line }
    }

    /**
     * Configures git in the set path using the passed in cc-to-git Stream
     * @param stream The Stream to use for configuration
     */
    static void configureRepository(GitOptions gitOptions) {
        log.debug("Entering configureRepository().")
        callOrDie("config", "user.name", gitOptions.user)
        log.info("Set git user.name to {}.", gitOptions.user)
        callOrDie("config", "user.email", gitOptions.email)
        log.info("Set git user.email to {}.", gitOptions.email)
        def gitIgnore = new File(path, '.gitignore');
        if (gitIgnore.exists()) FileUtils.forceDelete(gitIgnore)
        gitOptions.ignore.each { rule ->
            FileUtils.writeStringToFile(gitIgnore, rule + '\n', true)
            log.info("Added {} to .gitignore.", rule)
        }
        log.debug("Exiting configureRepository().")
    }

    /**
     * Checkout a branch, if it doesn't exist, create it and check it out.
     * @param branch the branch to check out
     */
    static void forceCheckout(String branch) {
        if (!Git.call("rev-parse --verify " + branch))
            Git.callOrDie("checkout " + branch)
        else
            Git.callOrDie("checkout -b " + branch)
    }
}
