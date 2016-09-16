package toGit.migration.targets.artifactory.context

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import toGit.context.base.Context
import toGit.migration.targets.artifactory.actions.Publish

trait ArtifactoryActionsContext implements Context {
    final static Logger log = LoggerFactory.getLogger(ArtifactoryActionsContext.class)

    void publish() {
        actions.add(new Publish())
        log.info("Registered 'publish' action.")
    }
}
