package toGit.migration.targets.artifactory.context

import groovy.util.logging.Log
import toGit.context.base.Context
import toGit.context.traits.TargetContext
import toGit.migration.targets.artifactory.ArtifactoryOptions
import toGit.migration.targets.artifactory.ArtifactoryTarget

@Log
class ArtifactoryTargetContext implements Context, TargetContext {

    public ArtifactoryTargetContext() {
        target = new ArtifactoryTarget(options: new ArtifactoryOptions())
    }

    void user(String user) {
        (target as ArtifactoryTarget).options.user = user
        log.info("Set user to $user")
    }

    void password(String password) {
        (target as ArtifactoryTarget).options.password = password
        log.info("Set password")
    }

    void url(String url) {
        (target as ArtifactoryTarget).options.url = url
        log.info("Set url to $url")
    }

    void repository(String repository) {
        (target as ArtifactoryTarget).options.repository = repository
        log.info("Set repository to $repository")
    }
}
