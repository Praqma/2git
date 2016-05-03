package all2all.migration.sources.mercurial

import all2all.migration.plan.Snapshot


class MercurialRepo {

    //the id is the where/it/is from the .hgsub defining all the subrepos in mercurial
    String id, repoName

    //maps mercurial changeset ids to git shas
    List<Snapshot> mercChangeSets = new ArrayList<Snapshot>()
    //did not want to introduce a new object, maybe later
    List<Snapshot> gitShas = new ArrayList<Snapshot>()

    Map<String, String> mercGitShas = [:]

    MercurialRepo(id, repoName) {

        this.id = id
        this.repoName = repoName
    }

    Process export() {
        def builder = new ProcessBuilder(
                "bash", "-c", "cd output/source/sourceClone/$repoName; hg bookmark -r default master;hg gexport --debug;" +
                " git config --bool core.bare false;git reset HEAD  -- .")
        builder.redirectErrorStream(true);
        return builder.start();
    }

    void extractRevisionNumbers(boolean isMerc) {
        def command
        if(isMerc)
            {command = "hg log  -T \"{node},{date|shortdate}\\n\""}
        else
            {command = "git log --pretty=%H"}
        def builder = new ProcessBuilder(
                "bash", "-c", "cd output/source/sourceClone/$repoName; $command"
        )
        builder.redirectErrorStream(true);
        def pr = builder.start();
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String output = null;
        while ((output = stdInput.readLine()) != null) {
            def values = output.split(',')
            MercurialChangeSet commit = new MercurialChangeSet(values[0])
            if(isMerc)
                {mercChangeSets.add(commit)}
            else
                {gitShas.add(commit)}
        }
        if(isMerc)
            {mercChangeSets = mercChangeSets.reverse()
            }
        else
            {gitShas = gitShas.reverse()}


    }

    void setMergedList() {
        mercChangeSets.eachWithIndex { val, idx ->
            mercGitShas[val.identifier] = gitShas.get(idx).identifier
        }
    }

    // *********************************** Getters, setters ***********************************


    List<Snapshot> getGitShas() {
        return gitShas
    }

    void setGitShas(List<Snapshot> gitShas) {
        this.gitShas = gitShas
    }

    List<Snapshot> getMercChangeSets() {
        return mercChangeSets
    }

    void setMercChangeSets(List<Snapshot> mercChangesSet) {
        this.mercChangeSets = mercChangesSet
    }

    Map<String, String> getMercGitShas() {
        return mercGitShas
    }

    void setMercGitShas(Map<Snapshot, Snapshot> mercGitShas) {
        this.mercGitShas = mercGitShas
    }

    String getRepoName() {
        return repoName
    }

    void setRepoName(String repoName) {
        this.repoName = repoName
    }




}