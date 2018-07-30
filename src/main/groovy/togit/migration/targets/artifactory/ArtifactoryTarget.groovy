package togit.migration.targets.artifactory

import static org.jfrog.artifactory.client.ArtifactoryClient.create

import org.jfrog.artifactory.client.Artifactory
import org.slf4j.LoggerFactory
import togit.migration.targets.MigrationTarget
import togit.migration.targets.artifactory.actions.Publish

class ArtifactoryTarget implements MigrationTarget {

    final static LOG = LoggerFactory.getLogger(this.class)

    Artifactory client
    ArtifactoryOptions options

    @Override
    void prepare() {
    }

    @Override
    void cleanup() {
        client.close()
    }

    /**
     * Publish given file to this Artifactory
     * @param path The artifact path to publish to
     * @param file The file to publish
     */
    void publish(String path, File file) {
        addAction('publish', new Publish(getClient(), options.repository, path, file))
    }

    Artifactory getClient() {
        client = client ?: create(options.url, options.user, options.password)
        client
    }
}
