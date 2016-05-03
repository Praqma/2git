package all2all.migration.sources.mercurial

import java.io.BufferedReader;
import java.nio.file.Files
import java.nio.file.Paths


class MercurialRepoPlan  {

    String sourceRepoPath
    String sourceRepoName
    List<MercurialRepo> repos = []
    Map<String,String> structure = [:]




    void MercurialPlan(sourceRepoPath, sourceRepoName) {
        this.sourceRepoPath = sourceRepoPath
        this.sourceRepoName = sourceRepoName
    }

    void modelAllRepos(String repoName){
        readStructure()
        createRepos()
        exportRepos()
        buildPlan()
    }

    void readStructure() {
        String file = new File(sourceRepoPath+"/.hgsub").absolutePath
        String fileContent = new File(file).text
        fileContent.eachLine { line ->
            def (whereIs, fromWhere) = line.tokenize("= ")
            structure.put(whereIs, fromWhere)
        }
    }

    String getSourceRepoPath() {
        return sourceRepoPath
    }

    void setSourceRepoPath(String sourceRepoPath) {
        this.sourceRepoPath = sourceRepoPath
    }

    String getRepoName() {
        return repoName
    }

    void setRepoName(String repoName) {
        this.repoName = repoName
    }

    List<MercurialRepo> getRepos() {
        return repos
    }

    void setRepos(List<MercurialRepo> repos) {
        this.repos = repos
    }

    Map<String, String> getStructure() {
        return structure
    }

    void setStructure(Map<String, String> structure) {
        this.structure = structure
    }
}
