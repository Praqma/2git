package examples

@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.7.1')
import groovyx.net.http.*

def repo = new BitbucketRepo("http://localhost:7990", "PROJ/funky-repo", "admin:password")

source('ccucm') {
    component "myComp@\\vob"
    stream "myStream@\\vob"
}

target('git') {
    user 'johan'
    email 'joa@praqma.net'
}

migrate {
    filters {
        filter {
            criteria {
                afterDate 'dd-MM-yyy', '01-01-2015'
            }
            extractions {
                baselineProperty([myBaselineName: 'shortname'])
            }
            actions {
                // Scrub Git repository, so file deletions will also be committed
                custom {
                    new File(target.workspace).eachFile { file ->
                        if(!file.name.startsWith(".git")) file.delete()
                    }
                }

                // Copy ClearCase view into Git repository
                copy(source.workspace, target.workspace)

                // Commit everything
                cmd 'git add .', target.workspace
                cmd 'git commit -m "$myBaselineName"', target.workspace
            }
        }
    }
    after {
        actions {
            custom {
                if (!repo.exists())
                    repo.create()
                repo.push(target.workspace)
            }
        }
    }
}

class BitbucketRepo {
    HTTPBuilder http
    Writable credentialsBase64

    String server       // http://myBitBucket.org
    String credentials  // bob:s3cr3t123
    String project      // MATH
    String repo         // fancy-calculator

    BitbucketRepo(String server, String repo, String credentials) {
        this.server = server
        this.credentials = credentials

        this.http = new HTTPBuilder(server)
        http.ignoreSSLIssues()
        this.credentialsBase64 = credentials.bytes.encodeBase64()

        this.project = repo.split("/")[0]
        this.repo = repo.split("/")[1]
    }

    boolean exists() {
        println "Checking if $project/$repo exists"

        boolean exists = false
        http.request(Method.GET) { req ->
            uri.path = "/rest/api/1.0/projects/$project/repos/$repo"
            headers.'Authorization' = "Basic $credentialsBase64"

            response.success = { resp, reader ->
                println "Got response: ${resp.statusLine}"
                exists = true
            }

            response.'404' = { resp, reader ->
                println "Got response: ${resp.statusLine}"
                println "Guess not"
            }
        }
        return exists
    }

    def create() {
        println "Creating $project/$repo"
        http.request(Method.POST) { req ->
            uri.path = "/rest/api/1.0/projects/$project/repos"
            headers.'Authorization' = "Basic $credentialsBase64"
            requestContentType = ContentType.JSON

            body = [name: repo]

            response.success = { resp ->
                println "Success! ${resp.status}"
            }

            response.failure = { resp ->
                println "Request failed with status ${resp.status}"
            }
        }
    }

    def push(String directory) {
        println "Pushing $directory to Bitbucket"
        def dir = new File(directory)
        def serverName = server.split("://")[1]

        def out = new StringBuffer(), err = new StringBuffer()
        def push = "git push http://$credentials@$serverName/scm/git/$repo master".execute([], dir)
        push.consumeProcessOutput(out, err)
        push.waitForOrKill(3600)
        println push.exitValue() + ": $err"
    }
}