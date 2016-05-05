package all2all.migration.sources.mercurial

class MercurialRepoPlan {

    String sourceRepoPath
    String sourceRepoName
    List<MercurialRepo> repos = []
    Map<String, String> structure = [:]


    MercurialRepoPlan(String sourceRepoPath, String sourceRepoName) {
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

    void createRepos() {
        structure.each { key, value ->
            MercurialRepo r = new MercurialRepo(key, value, sourceRepoName, true)
            repos.add(r)
        }
    }

    void exportRepos() {
        //run the export method on all repos
        //run the extract revision numbers
        //run the setMerged lists on all
    }

}
