multibranchPipelineJob('2gitPipeline') {
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
