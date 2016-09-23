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
    }

    @Override
    void cleanup() {
        client.close()
    }

    def publish(String path, File file) {
        def pub = new Publish(getClient(), options.repository, path, file)
        addAction('publish', pub)
    }

    Artifactory getClient() {
        if(!client) client = create(options.url, options.user, options.password)
        return client
    }
}
