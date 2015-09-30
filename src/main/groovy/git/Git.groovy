package git

import migration.Stream
@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
import net.praqma.util.execute.CommandLine
import org.apache.commons.io.FileUtils

/**
 * A class that facilitates calling Git commands
 */
@Slf4j
class Git {
    static String path = "./output" // The path the Git commands will be run in

    /**
     * Calls git with given arguments in the set path.
     * @param args The arguments to call git with.
     */
    static void call(String... args) {
        log.debug("Entering call().")
        String cmd = "git " + args.join(" ");
        log.info("Running '{}' in {}", cmd, path)
        CommandLine.newInstance().run(cmd, new File(path)).stdoutBuffer.eachLine { line -> println line }
        log.debug("Exiting call().")
    }

    /**
     * Configures git in the set path using the passed in cc-to-git Stream
     * @param stream The Stream to use for configuration
     */
    static void configureRepository() {
        log.debug("Entering configureRepository().")
        def user = "cc-to-git";
        call("config", "user.name", user)
        log.info("Set git user.name to {}.", user)
        def mail = "support@praqma.net"
        call("config", "user.email", mail)
        log.info("Set git user.email to {}.", mail)
        /*def gitIgnore = new File(path, '.gitignore');
        if (gitIgnore.exists()) FileUtils.forceDelete(gitIgnore)
        stream.gitIgnore.each { rule ->
            FileUtils.writeStringToFile(gitIgnore, rule + '\n', true)
            log.info("Added {} to .gitignore.", rule)
        }*/
        log.debug("Exiting configureRepository().")
    }
}
