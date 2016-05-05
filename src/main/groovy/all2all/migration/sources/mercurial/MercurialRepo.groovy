package all2all.migration.sources.mercurial

import all2all.migration.plan.Snapshot


class MercurialRepo {

    //the id is the where/it/is from the .hgsub defining all the subrepos in mercurial
    //from is different from id if the repo is a remote repo
    String id, from, repoName
    boolean isSubrepo

    //maps mercurial changeset ids to git shas
    List<Snapshot> mercChangesets = new ArrayList<Snapshot>()
    //did not want to introduce a new object, maybe later
    List<Snapshot> gitShas = new ArrayList<Snapshot>()

    Map<String, String> mercGitShas = [:]


    MercurialRepo(id, from, repoName, isSubrepo) {

        this.id = id
        this.from = from
        this.repoName = repoName
        this.isSubrepo = isSubrepo

        if(isSubrepo) extendRepoNameIfSubrepo()
    }

    void extendRepoNameIfSubrepo(){
        repoName = repoName+"/"+id;
    }

    Process export() {
        def builder = new ProcessBuilder("bash", "-c", "cd output/source/sourceClone/$repoName; " +
                "hg bookmark -r default master;hg gexport --debug; " +
                "git config --bool core.bare false;git reset HEAD  -- .")
        builder.redirectErrorStream(true)
        return builder.start()
    }

    void extractRevisionNumbers(boolean isMerc) {
        def command
        if (isMerc) {
            command = "hg log  -T \"{node},{date|shortdate}\\n\""
        } else {
            command = "git log --pretty=%H"
        }
        def builder = new ProcessBuilder(
                "bash", "-c", "cd output/source/sourceClone/$repoName; $command"
        )
        builder.redirectErrorStream(true)
        def pr = builder.start()
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(pr.getInputStream()))
        String output
        while ((output = stdInput.readLine()) != null) {
            def values = output.split(',')
            MercurialChangeset commit = new MercurialChangeset(values[0])
            if (isMerc) {
                mercChangesets.add(commit)
            } else {
                gitShas.add(commit)
            }
        }
        if (isMerc) {
            mercChangesets = mercChangesets.reverse()
        } else {
            gitShas = gitShas.reverse()
        }
    }

    void setMergedList() {
        mercChangesets.eachWithIndex { val, idx ->
            mercGitShas[val.identifier] = gitShas.get(idx).identifier
        }
    }
}