multibranchPipelineJob('2gitPipeline') {
  triggers {
    periodic(5)
  }
  branchSources {
    git {
      remote('git@github.com:Praqma/2git.git')
      credentialsId('jenkins')
      includes('*')
    }
  }
  orphanedItemStrategy {
    discardOldItems {
      numToKeep(20)
    }
  }
}
