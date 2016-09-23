package toGit.migration.targets.artifactory

import groovy.util.logging.Log
import org.jfrog.artifactory.client.Artifactory
import toGit.migration.targets.MigrationTarget
import toGit.migration.targets.artifactory.actions.Publish
import static org.jfrog.artifactory.client.ArtifactoryClient.create

@Log
class ArtifactoryTarget implements MigrationTarget {
    Artifactory client
    ArtifactoryOptions options

    @Override
    void prepare() {
        client = create(options.url, options.user, options.password)
    }

    @Override
    void cleanup() {

    }

    def publish(String path, File file) {
        def pub = new Publish(client, options.repository, path, file)
        addAction('publish', pub)
    }
}
