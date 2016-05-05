package all2all.migration.sources.mercurial

import org.junit.Before
import org.junit.Test

public class MercurialRepoPlanTest {

    MercurialRepoPlan plan = new MercurialRepoPlan("/Users/angeloron/Repos/mercurial2","mercurial2")

//    @Before
//    public void setUp() {
//        String repoPath = "output/source/sourceClone/mercurial2"
//        plan.sourceRepoPath = repoPath
//    }

    // **************************************** readStructure ****************************************
    @Test
    public void testReadStructureLength() {
        plan.readStructure()
        assert plan.structure.size() == 3
    }

    @Test
    public void testReadStructureFirst() {
        plan.readStructure();
        assert plan.structure["nested"].equals("nested")
    }

    @Test
    public void testReadStructureLast() {
        plan.readStructure();
        assert plan.structure["nested3"].equals("nested3")
    }

    // **************************************** createRepos ****************************************
    @Test
    public void testCreateRepos() {
        plan.readStructure()
        plan.createRepos()
        assert plan.structure.size() == 3
    }

    @Test
    public void testCreateReposFirst() {
        plan.readStructure()
        plan.createRepos()
        MercurialRepo r = plan.repos.get(0)
        assert r.id.equals("nested")
    }

    @Test
    public void testCreateReposLast() {
        plan.readStructure()
        plan.createRepos()
        MercurialRepo r = plan.repos.get(2)
        assert r.id.equals("nested3")
    }
}