package all2all.migration.sources.mercurial;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MercurialRepoPlanTest {

    MercurialRepoPlan plan = new MercurialRepoPlan();

    @Before
    public void setUp() {
        plan.setSourceRepoPath("output/source/sourceClone/mercurial2");
    }

    @Test
    public void readStructureLenghtTest() {
        plan.readStructure();
        assertTrue("The size of the structure map", plan.getStructure().size()== 3 );
    }

    @Test
    public void readStructureFirstTest() {
        plan.readStructure();
        assertEquals("The first element in the Map", plan.getStructure().get("nested"), "nested");
    }

    @Test
    public void readStructureLastTest() {
        plan.readStructure();
        assertEquals("The last element in the Map", plan.getStructure().get("nested3"), "nested3");
    }

}