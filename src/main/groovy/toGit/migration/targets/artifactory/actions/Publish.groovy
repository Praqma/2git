package toGit.migration.targets.artifactory.actions

import org.jfrog.artifactory.client.Artifactory
import toGit.migration.plan.Action

class Publish extends Action {
    Artifactory client
    String repository
    String path
    File file

    Publish(Artifactory client, String repository, String path, File file) {
        this.client = client
        this.repository = repository
        this.path = path
        this.file = file
    }

    @Override
    void act(HashMap<String, Object> extractionMap) {
        client.repository(repository).upload(path, file).doUpload()
    }
}