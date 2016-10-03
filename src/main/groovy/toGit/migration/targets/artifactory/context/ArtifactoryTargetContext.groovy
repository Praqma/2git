package toGit.migration.targets.artifactory.context

import org.slf4j.LoggerFactory
import toGit.context.base.Context
import toGit.context.traits.TargetContext
import toGit.migration.targets.artifactory.ArtifactoryOptions
import toGit.migration.targets.artifactory.ArtifactoryTarget

class ArtifactoryTargetContext implements TargetContext {

    final static log = LoggerFactory.getLogger(this.class)

    public ArtifactoryTargetContext() {
        target = new ArtifactoryTarget(options: new ArtifactoryOptions())
    }

    void user(String user) {
        (target as ArtifactoryTarget).options.user = user
        log.debug("Set user to $user")
    }

    void password(String password) {
        (target as ArtifactoryTarget).options.password = password
        log.debug("Set password")
    }

    void url(String url) {
        (target as ArtifactoryTarget).options.url = url
        log.debug("Set url to $url")
    }

    void repository(String repository) {
        (target as ArtifactoryTarget).options.repository = repository
        log.debug("Set repository to $repository")
    }
}
