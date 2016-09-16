package toGit.context

import org.junit.Test
import toGit.Executor
import toGit.TestHelper
import toGit.migration.MigrationManager
import toGit.migration.sources.dummy.DummySource
import toGit.migration.targets.dummy.DummyTarget

public class ExtractionsContextTest {

    private static void testExtraction(String extraction) {
        def commandFile = TestHelper.createCommandFile(actionScriptFor(extraction))
        new Executor().execute(commandFile.absolutePath)
        assertPlan()
    }

    static String actionScriptFor(String extraction) {
        def script = $/
        source('dummy')
        target('dummy')
        migrate(true) {
            filters {
                filter {
                    extractions {
                        $extraction
                    }
                }
            }
        }/$
        return script.stripIndent()
    }

    private static void assertPlan() {
        def manager = MigrationManager.instance
        assert manager.source instanceof DummySource
        assert manager.targets.values()[0] instanceof DummyTarget

        assert manager.plan != null
        assert manager.plan.befores.size() == 0
        assert manager.plan.afters.size() == 0
        assert manager.plan.filters.size() == 1

        assert manager.plan.filters[0].criteria.size() == 0
        assert manager.plan.filters[0].extractions.size() == 1
        assert manager.plan.filters[0].actions.size() == 0
        assert manager.plan.filters[0].filters.size() == 0
    }


    @Test
    public void testCustom() throws Exception {
        testExtraction("custom { println 'hi' }")
    }
}