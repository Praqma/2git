package toGit.migration.targets.artifactory.actions

import groovyx.net.http.RESTClient
import org.apache.http.entity.FileEntity
import toGit.migration.plan.Action

class Publish extends Action {
    RESTClient client
    def path
    def file

    Publish(def server, def port, def username, def password, def artifactoryPath, def fileToPub) {
        def artifactoryUrl = "http://$server:$port"
        client = new RESTClient(artifactoryUrl)
        if(username && password)
            client.auth.basic(username, password)
        client.encoder.'application/zip' = this.&encodeZipFile

        path = artifactoryPath
        file = fileToPub
    }

    @Override
    void act(HashMap<String, Object> extractionMap) {
        client.put(path: path,
                body: file,
                requestContentType: 'application/zip'
        )
        println 'Published $file'
    }

    static def encodeZipFile(Object data) throws UnsupportedEncodingException {
        def entity = new FileEntity((File) data, 'application/zip');
        entity.setContentType('application/zip');
        return entity
    }
}