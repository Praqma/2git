package togit.context.dummy

import org.junit.Test
import togit.Executor
import togit.TestHelper
import togit.migration.MigrationManager
import togit.migration.sources.dummy.DummySource
import togit.migration.targets.dummy.DummyTarget

class ExtractionsContextTest {

    private static void testExtraction(String extraction) {
        new Executor().execute(TestHelper.tempCommandFile(actionScriptFor(extraction)).absolutePath)
        assertPlan()
    }

    static String actionScriptFor(String extraction) {
        $/
        source('dummy')
        target('dummy')
        migrate(true, true) {
            filters {
                filter {
                    extractions {
                        $extraction
                    }
                }
            }
        }/$.stripIndent()
    }

    private static void assertPlan() {
        MigrationManager manager = MigrationManager.instance
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
    void testCustom() {
        testExtraction("custom { println 'hi' }")
    }
}
