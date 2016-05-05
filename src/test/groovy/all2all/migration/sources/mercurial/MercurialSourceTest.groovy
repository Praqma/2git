package all2all.migration.sources.mercurial

import org.junit.Before;
import org.junit.Test;


public class MercurialSourceTest {

    MercurialSource source = new MercurialSource()

    @Before
    public void setUp() {
        source.sourceRepo = "/Users/angeloron/Repos/mercurial2"
        source.repoName = "mercurial2"
    }

    @Test
    public void testCloneSubrepos() throws Exception {
        source.cloneRemote()
        source.cloneSubrepos().waitFor()
        def nestedDir = new File(source.sourceRepo + "/nested")
        def nestedDir2 = new File(source.sourceRepo + "/nested2")
        def nestedDir3 = new File(source.sourceRepo + "/nested3")
        assert nestedDir.exists() && nestedDir2.exists() && nestedDir3.exists()
    }
}