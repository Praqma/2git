/** CONFIG **/
REPO = "github.com/Praqma/2git.git"
INTEGRATION_BRANCH = 'master'
BRANCH_PREFIX = 'ready/'
CREDENTIALS_ID = "github"

/** STATE **/
inputSHA = ""
authorName = "none"
authorEmail = "none"

/** PIPELINE **/

println("[PARAMETERS]" +
        "\n\tbranch: ${BRANCH_NAME}" +
        "\n\ttarget: ${INTEGRATION_BRANCH}" +
        "\n\tmerge: ${shouldMerge()}")

lockIf(shouldMerge(), "integration-lock") {
    node('utility-slave') {
        guardedStage("checkout") {
            deleteDir()
            checkout scm
            docker.image('bravissimolabs/alpine-git:latest').inside {
                inputSHA = sh(script: "git rev-parse origin/${BRANCH_NAME}", returnStdout: true).trim()
                authorName = sh(script: "git log -1 --format='%an' ${inputSHA}", returnStdout: true).trim()
                authorEmail = sh(script: "git log -1 --format='%ae' ${inputSHA}", returnStdout: true).trim()

                timeout(2) {
                    if (shouldMerge()) {
                        sh """\
                        git config user.email "${authorEmail}"
                        git config user.name "${authorName}"
                        git checkout "${INTEGRATION_BRANCH}"
                        if [ "\$(git branch --contains ${inputSHA} | wc -l)" -gt "0" ]
                        then
                            echo "MERGE ERROR: origin/${BRANCH_NAME} already present in origin/${INTEGRATION_BRANCH}"
                            exit 1
                        fi
                            COMMITS="\$(git log --oneline ${INTEGRATION_BRANCH}..${inputSHA} | wc -l)"
                            if [ "\${COMMITS}" -gt "1" ] || ! git merge --ff-only ${inputSHA}
                            then
                                git reset --hard origin/${INTEGRATION_BRANCH}
                                git merge --no-ff --no-commit ${inputSHA}
                                git commit --author "${authorName} <${authorEmail}>" --message "Merge branch '${
                            BRANCH_NAME
                        }' into '${INTEGRATION_BRANCH}'"
                        fi""".stripIndent()
                    } else {
                        sh "git checkout ${BRANCH_NAME}"
                    }
                }
                stash name: "merge-result", includes: "**", useDefaultExcludes: false
            }
        }
    }

    node('utility-slave') {
        guardedStage("build") {
            deleteDir()
            docker.image('drbosse/gradle-git:3.5-jre-alpine').inside {
                unstash "merge-result"
                sh "gradle build --stacktrace"
            }
        }
    }

    node('utility-slave') {
        guardedStage("push") {
            deleteDir()
            if (shouldMerge()) {
                docker.image('bravissimolabs/alpine-git:latest').inside {
                    unstash "merge-result"
                    withCredentials([[$class: 'UsernamePasswordMultiBinding',
                            credentialsId: "$CREDENTIALS_ID",
                            passwordVariable: 'PASSWORD',
                            usernameVariable: 'USERNAME']]) {
                        timeout(1) {
                            sh """\
                                git push https://\${USERNAME}:\${PASSWORD}@${REPO} ${INTEGRATION_BRANCH}
                                git push https://\\${USERNAME}:\\${PASSWORD}@${REPO} :${BRANCH_NAME}""".stripIndent()
                        }
                    }
                }
            }
        }
    }
}

// Flyweight executor
guardedStage("promotion"){
    timeout(time: 1, unit: 'HOURS') {
        input "Promote? (Don't forget to tag!)"
    }
}

node('utility-slave') {
    guardedStage("release"){
        deleteDir()
        docker.image('drbosse/gradle-git:3.5-jre-alpine').inside {
            unstash "merge-result"
            withCredentials([string(credentialsId: '2git-token', variable: 'GITHUB_TOKEN')]) {
                sh "gradle githubRelease --stacktrace -PGITHUB_TOKEN=\$GITHUB_TOKEN"
            }
        }
    }
}

pipelineSuccess()

/** UTILITY METHODS **/

// Checks if the branch should be merged in
boolean shouldMerge() { env.BRANCH_NAME.startsWith(BRANCH_PREFIX) }

// Used to lock stages so only one branch gets integrated at a time
def lockIf(condition, name, closure) {
    if (condition) {
        println "Running under lock: ${name}"
        lock(name, closure)
    } else {
        println "Running without lock"
        closure.call()
    }
}

// Catch failures to send mails
def guardedStage(name, closure) {
    try {
        stage(name, closure)
    } catch (e) {
        pipelineFailure(name, e)
        throw e
    }
}

def pipelineSuccess() {
    node("master") {
        currentBuild.result = 'SUCCESS'
    }
}

def pipelineFailure(stage, exception) {
    node("master") {
        currentBuild.result = 'FAILURE'
    }
}
