// 2git pipeline

multibranchPipelineJob('2gitPipeline') {
  displayName('2git pipeline')
  configure {
    it / sources / data / "jenkins.branch.BranchSource" / "source" (class: "org.jenkinsci.plugins.github_branch_source.GitHubSCMSource") << {
      scanCredentialsId "github"
      id "2git-github"
      repoOwner "Praqma"
      repository "2git"
      traits {
        "org.jenkinsci.plugins.github__branch__source.BranchDiscoveryTrait" {
          strategyId 1
        }
      }
    }
    it / sources / data / "jenkins.branch.BranchSource" / strategy (class: "jenkins.branch.DefaultBranchPropertyStrategy") {
      properties (class: "java.util.Arrays\$ArrayList") {
        a (class: "jenkins.branch.BranchProperty-array") {
          "jenkins.branch.NoTriggerBranchProperty" {}
        }
      }
    }        
  }
  orphanedItemStrategy {
    discardOldItems {
      numToKeep(20)
      daysToKeep(360)
    }
  }
}

// 2git seed job

pipelineJob ('2git-pipeline-seed') {
  triggers {
    cron('@midnight')
  }
  definition {
    cps {
      script("""\
        import javaposse.jobdsl.plugin.*

        node("master") {
            git branch: "master", credentialsId: "github", url: "git@github.com:Praqma/2git.git"
            step([
                \$class: 'ExecuteDslScripts',
                targets: "Jenkinsfile.dsl",
                ignoreMissingFiles: true,
                ignoreExisting: false,
                removedJobAction: RemovedJobAction.DELETE,
                removedViewAction: RemovedViewAction.DELETE,
                lookupStrategy: LookupStrategy.JENKINS_ROOT,
                additionalClasspath: ""
            ])
        }""".stripIndent())
      }
  }
}

listView('2git') {
  jobs {
    regex('2git.+')
  }
  columns {
    status()
    weather()
    name()
    buildButton()
  }
}
