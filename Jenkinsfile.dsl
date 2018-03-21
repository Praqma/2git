multibranchPipelineJob('2gitPipeline') {
  displayName('2git pipeline')
  branchSources {
    gitHub {
      repoOwner('Praqma')
      repository('2git.git')
      scanCredentialsId('jenkins')
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
