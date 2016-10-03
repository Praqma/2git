package toGit.migration.targets.artifactory.actions

import org.jfrog.artifactory.client.Artifactory
import org.slf4j.LoggerFactory
import toGit.migration.plan.Action

class Publish extends Action {

    final static log = LoggerFactory.getLogger(this.class)

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
        log.info("Publishing ${file.name} to Artifactory")
        client.repository(repository).upload(path, file).doUpload()
        log.info("Published ${file.name} to Artifactory")
    }
}