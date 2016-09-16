package toGit.migration.targets.artifactory.context

import groovy.util.logging.Log
import toGit.context.base.Context
import toGit.context.traits.TargetContext
import toGit.migration.targets.artifactory.ArtifactoryOptions
import toGit.migration.targets.artifactory.ArtifactoryTarget

@Log
class ArtifactoryTargetContext implements Context, TargetContext {

    /**
     * GitOptionsContext constructor
     */
    public ArtifactoryTargetContext() {
        target = new ArtifactoryTarget(options: new ArtifactoryOptions())
    }

    void user(String user) {
        target.options.user = user
        log.info("Set user to $user.")
    }

    void password(String password) {
        target.options.password = password
        log.info("Set user to $password.")
    }

    void url(String url) {
        target.options.user = url
        log.info("Set user to $url.")
    }
}
