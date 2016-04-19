package migration.sources.mercurial

import context.CriteriaContext
import context.ExtractionsContext
import context.base.Context
import migration.plan.Criteria
import migration.sources.MigrationSource
import migration.sources.Snapshot
import migration.sources.mercurial.context.MercurialCriteriaContext
import migration.sources.mercurial.context.MercurialExtractionsContext

class MercurialSource implements MigrationSource {

    String branch
    String sourceRepo
    String repoName
    boolean hasSubrepos
    Runtime rt = Runtime.getRuntime()
    ProcessBuilder builder
    Process pr, pr2

    @Override
    List<Snapshot> getSnapshots(List<Criteria> criteria) {
        builder = new ProcessBuilder(
                "bash", "-c", "cd output/source/sourceClone/$repoName; hg log  -T \"{node},{date|shortdate}\\n\"")
        builder.redirectErrorStream(true);
        pr = builder.start();
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String output = null;
        List<Snapshot> snapshots = []
        while ((output = stdInput.readLine()) != null) {
            def values = output.split(',')
            MercurialChangeSet commit = new MercurialChangeSet(values[0])
            commit.date = new Date().parse("yyyy-MM-dd", values[1])
            snapshots.add(commit)
        }
        return snapshots
    }

//    //wo criteria test version
//    List<Snapshot> getSnapshots() {
//        builder = new ProcessBuilder(
//                "bash", "-c","cd output/source/sourceClone/$repoName; hg log  --template \"{node}\\n\"")
//        builder.redirectErrorStream(true);
//        pr = builder.start();
//        BufferedReader stdInput = new BufferedReader(new
//                InputStreamReader(pr.getInputStream()));
//        String s = null;
//        List<Snapshot> snapshots = []
//        while ((s = stdInput.readLine()) != null) {
//            MercurialChangeSet commit = new MercurialChangeSet(s)
//            println commit.identifier
//            snapshots.add(commit)
//        }
//        return snapshots
//
//    }

    @Override
    void checkout(Snapshot snapshot) {
        def id = ((MercurialChangeSet) snapshot).identifier
        File f = new File(workspace)
        if (f.exists() && f.isDirectory()) {
            pr = rt.exec("rm -R $workspace")
        }
        builder = new ProcessBuilder(
                "bash", "-c", "cd output;mkdir source; cd source;mkdir temp; cd temp;  hg clone -r $id $sourceRepo")
        builder.redirectErrorStream(true);
        Process p = builder.start();
    }

//    //pre snapshot testing
//    void checkout(String id) {
//        File f = new File(dir)
//        if (f.exists() && f.isDirectory()) {
//            pr = rt.exec("rm -R $dir")
//        }
//        builder = new ProcessBuilder(
//                "bash", "-c","cd output;mkdir source; cd source;mkdir temp; cd temp;  hg clone -r $id $sourceRepo")
//        builder.redirectErrorStream(true);
//        Process p = builder.start();
//    }

    @Override
    void prepare() {
        if (hasSubrepos) {
            setupSubrepos() {}
        }

        cloneRemote()

        //checkout ("66c87efa37b1")
        //getSnapshots()
    }

    @Override
    void cleanup() {

    }

    @Override
    Context withCriteria(CriteriaContext criteriaContext) {
        return criteriaContext as MercurialCriteriaContext
    }

    @Override
    Context withExtractions(ExtractionsContext extractionsContext) {
        return extractionsContext as MercurialExtractionsContext
    }

    void cloneRemote() {
        builder = new ProcessBuilder(
                "bash", "-c", "cd output;mkdir source; cd source; mkdir sourceClone; cd sourceClone; hg clone $sourceRepo")
        builder.redirectErrorStream(true);
        Process p = builder.start();
    }
}
