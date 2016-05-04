package all2all.migration.sources.mercurial

class MercurialRepoPlan {

    String sourceRepoPath
    String sourceRepoName
    List<MercurialRepo> repos = []
    Map<String, String> structure = [:]


    void MercurialPlan(sourceRepoPath, sourceRepoName) {
        this.sourceRepoPath = sourceRepoPath
        this.sourceRepoName = sourceRepoName
    }

    void modelAllRepos(String repoName) {
        readStructure()
        createRepos()
        exportRepos()
        buildPlan()
    }

    void readStructure() {
        String file = new File(sourceRepoPath + "/.hgsub").absolutePath
        String fileContent = new File(file).text
        fileContent.eachLine { line ->
            def (whereIs, fromWhere) = line.tokenize("= ")
            structure.put(whereIs, fromWhere)
        }
    }
}
