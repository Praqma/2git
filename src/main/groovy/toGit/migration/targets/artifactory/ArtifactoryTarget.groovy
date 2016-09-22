package toGit.migration.targets.artifactory

import groovy.util.logging.Log
import toGit.migration.targets.MigrationTarget
import toGit.migration.targets.artifactory.actions.Publish

@Log
class ArtifactoryTarget implements MigrationTarget {
    ArtifactoryOptions options

    @Override
    void prepare() {

    }

    @Override
    void cleanup() {

    }

    def publish(String artifactoryPath, File fileToPublish) {
        def pub = new Publish(options.host,
                              options.port,
                              options.user,
                              options.password,
                              artifactoryPath,
                              fileToPublish)
        addAction('publish', pub)
    }
}
