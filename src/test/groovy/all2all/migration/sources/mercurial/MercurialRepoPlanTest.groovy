package all2all.migration.sources.mercurial

import org.junit.Before
import org.junit.Test

public class MercurialRepoPlanTest {

    MercurialRepoPlan plan = new MercurialRepoPlan()

    @Before
    public void setUp() {
        String repoPath = "output/source/sourceClone/mercurial2"
        plan.sourceRepoPath = repoPath
    }

    @Test
    public void readStructureLengthTest() {
        plan.readStructure()
        assert plan.structure.size() == 3
    }

    @Test
    public void readStructureFirstTest() {
        plan.readStructure();
        assert plan.structure["nested"].equals("nested")
    }

    @Test
    public void readStructureLastTest() {
        plan.readStructure();
        assert plan.structure["nested3"].equals("nested3")
    }

}