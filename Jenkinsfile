/** CONFIG **/
REPO = 'github.com/Praqma/2git.git'
INTEGRATION_BRANCH = 'master'
BRANCH_PREFIX = 'ready/'
CREDENTIALS_ID = 'github'

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
    node('utility-slave') {
        stage('checkout') {
            deleteDir()
            checkout scm
            docker.image('alpine/git:1.0.4').inside {
                // Collecting some intel
                inputSHA = sh(script: "git rev-parse origin/${BRANCH_NAME}", returnStdout: true).trim()
                authorName = sh(script: "git log -1 --format='%an' ${inputSHA}", returnStdout: true).trim()
                authorEmail = sh(script: "git log -1 --format='%ae' ${inputSHA}", returnStdout: true).trim()

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

                            if [ "\$(git branch --contains ${inputSHA} | wc -l)" -gt "0" ]
                            then
                                echo "MERGE ERROR: ${BRANCH_NAME} already exists in ${INTEGRATION_BRANCH}"
                                exit 1
                            fi
                            COMMITS="\$(git log --oneline ${INTEGRATION_BRANCH}..${inputSHA} | wc -l)"
                            if [ "\${COMMITS}" -gt "1" ] || ! git merge --ff-only ${inputSHA}
                            then
                                git reset --hard origin/${INTEGRATION_BRANCH}
                                git merge --no-ff --no-commit ${inputSHA}
                                git commit --author "${authorName} <${authorEmail}>" --message "Merge branch '${BRANCH_NAME}' into '${INTEGRATION_BRANCH}'"
                            fi""".stripIndent()
                    }
                }
                stash name: 'merge-result', includes: '**', useDefaultExcludes: false
            }
        }

        // Run a build
        stage('build') {
            deleteDir()
            docker.image('drbosse/gradle-git:4.5.0-jre8-alpine').inside {
                unstash 'merge-result'
                sh 'gradle build --stacktrace'
            }
        }

        // Publish the merge results
        stage('publish') {
            deleteDir()
            if (isIntegration()) {
                docker.image('alpine/git:1.0.4').inside {
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
stage('promotion'){
    timeout(time: 1, unit: 'HOURS') {
        input 'Promote? (Remember to tag!)'
    }
}

node('utility-slave') {
    stage('release'){
        deleteDir()
        docker.image('drbosse/gradle-git:4.5.0-jre8-alpine').inside {
            unstash 'merge-result'
            withCredentials([string(credentialsId: '2git-token', variable: 'GITHUB_TOKEN')]) {
                sh "./gradlew githubRelease --stacktrace -PGITHUB_TOKEN=\$GITHUB_TOKEN"
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
