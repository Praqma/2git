/** CONFIG **/
REPO = "github.com/Praqma/2git.git"
INTEGRATION_BRANCH = 'master'
BRANCH_PREFIX = 'ready/'
CREDENTIALS_ID = "releasepraqma-https"

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
    node {
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

    node {
        guardedStage("build") {
            deleteDir()
            docker.image('gradle:3.5-jre-alpine').inside {
                unstash "merge-result"
                sh "gradle build"
            }
        }
    }

    node {
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
        emailext(
                subject: "Success: ${BRANCH_NAME}",
                to: authorEmail,
                body: """\
                        Status: Succeeded
                        Branch: ${BRANCH_NAME}
                        Jenkins URL: ${BUILD_URL}""".stripIndent()
        )
    }
}

def pipelineFailure(stage, exception) {
    node("master") {
        emailext(
                subject: "Failure: ${BRANCH_NAME}: ${stage}",
                to: authorEmail,
                body: """\
                        Status: Failed at stage "${stage}" with "${exception.toString()}"
                        Branch: ${BRANCH_NAME}
                        Jenkins URL: ${BUILD_URL}""".stripIndent()
        )
    }
}
