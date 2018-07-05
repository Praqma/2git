/** CONFIG **/
REPO = 'github.com/Praqma/2git.git'
INTEGRATION_BRANCH = 'master'
BRANCH_PREFIX = 'ready/'
CREDENTIALS_ID = 'github'
DOCKER_NODE = 'dockerhost1'

/** STATE **/
inputSHA = ''
authorName = 'none'
authorEmail = 'none'

/** PIPELINE **/

println('[PARAMETERS]' +
        "\n\tbranch: ${BRANCH_NAME}" +
        "\n\ttarget: ${INTEGRATION_BRANCH}" +
        "\n\tmerge: ${isIntegration()}")

lockIf(isIntegration(), 'integration-lock') {
    node(DOCKER_NODE) {
        stage('checkout') {
            deleteDir()
            checkout scm
            docker.image('alpine/git:1.0.4').inside("--entrypoint=''") {
                // Collecting some intel
                inputSHA = sh(script: "git rev-parse origin/${BRANCH_NAME}", returnStdout: true).trim()
                authorName = sh(script: "git log -1 --format='%an' ${inputSHA}", returnStdout: true).trim()
                authorEmail = sh(script: "git log -1 --format='%ae' ${inputSHA}", returnStdout: true).trim()

                // Somehow these branches don't exist post-clone
                sh """\
                    git fetch origin ${INTEGRATION_BRANCH}:${INTEGRATION_BRANCH}
                    git fetch origin ${BRANCH_NAME}:${BRANCH_NAME}
                    """.stripIndent()

                // 5 minutes of patience
                timeout(5) {
                    if (!isIntegration()) {
                        // Not an integration build, no fancy merges required
                        sh "git checkout ${BRANCH_NAME}"
                    } else {
                        // Integration build, time to merge
                        sh """\
                            git config user.email "${authorEmail}"
                            git config user.name "${authorName}"

                            git checkout "${INTEGRATION_BRANCH}"
                            if [ "\$(git branch --contains ${inputSHA} | wc -l)" -gt "1" ]
                            then
                                echo "MERGE ERROR: ${BRANCH_NAME} already exists in ${INTEGRATION_BRANCH}"
                                exit 1
                            fi

                            git rebase ${INTEGRATION_BRANCH} ${BRANCH_NAME}
                            git checkout "${INTEGRATION_BRANCH}"
                            git merge --ff-only ${BRANCH_NAME}
                            """.stripIndent()
                    }
                }
                stash name: 'merge-result', includes: '**', useDefaultExcludes: false
            }
        }

        // Run a build
        stage('build') {
            deleteDir()
            docker.image('drbosse/gradle-git:4.5.0-jre8-alpine').inside("--entrypoint=''") {
                unstash 'merge-result'
                sh './gradlew clean build assembleDist --stacktrace'
                stash name: 'merge-result', includes: '**', useDefaultExcludes: false
            }
        }

        // Publish the merge results
        stage('publish') {
            deleteDir()
            if (isIntegration()) {
                docker.image('alpine/git:1.0.4').inside("--entrypoint=''") {
                    unstash 'merge-result'
                    withCredentials([[$class: 'UsernamePasswordMultiBinding',
                            credentialsId: "$CREDENTIALS_ID",
                            passwordVariable: 'PASSWORD',
                            usernameVariable: 'USERNAME']]) {
                        timeout(5) {
                            sh """\
                                git push https://\${USERNAME}:\${PASSWORD}@${REPO} ${INTEGRATION_BRANCH}
                                git push https://\${USERNAME}:\${PASSWORD}@${REPO} :${BRANCH_NAME}""".stripIndent()
                        }
                    }
                }
            }
        }
    }
}

// Flyweight executor
PROMOTE = false
stage('promotion'){
    try {
        timeout(time: 1, unit: 'HOURS') {
            // TODO: Automate tagging?
            input 'Promote? (Remember to tag!)'
        }
        PROMOTE = true
    } catch (Throwable t) {
        currentBuild.result = 'SUCCESS'
        return
    }
}

if (PROMOTE) {
    node(DOCKER_NODE) {
        stage('release'){
            deleteDir()
            docker.image('drbosse/gradle-git:4.5.0-jre8-alpine').inside("--entrypoint=''") {
                unstash 'merge-result'
                withCredentials([string(credentialsId: 'praqmarelease', variable: 'GITHUB_TOKEN')]) {
                    sh "./gradlew githubRelease --stacktrace -PGITHUB_TOKEN=\$GITHUB_TOKEN"
                }
            }
        }
    }
}

/** UTILITY METHODS **/

// Checks if the branch should be merged in
boolean isIntegration() { env.BRANCH_NAME.startsWith(BRANCH_PREFIX) }

// Used to lock stages so only one branch gets integrated at a time
def lockIf(condition, name, closure) {
    if (condition) {
        println "Running under lock: ${name}"
        lock(name, closure)
    } else {
        println 'Running without lock'
        closure.call()
    }
}
