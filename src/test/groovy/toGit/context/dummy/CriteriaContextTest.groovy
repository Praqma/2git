package togit.context.dummy

import org.junit.Test
import togit.Executor
import togit.TestHelper
import togit.migration.MigrationManager
import togit.migration.sources.dummy.DummySource
import togit.migration.targets.dummy.DummyTarget

class CriteriaContextTest {
    private static void testCriteria(String criteria) {
        new Executor().execute(TestHelper.tempCommandFile(actionScriptFor(criteria)).absolutePath)
        assertPlan()
    }

    static String actionScriptFor(String criteria) {
        $/
        source('dummy')
        target('dummy')
        migrate(true) {
            filters {
                filter {
                    criteria {
                        $criteria
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

        assert manager.plan.filters[0].criteria.size() == 1
        assert manager.plan.filters[0].extractions.size() == 0
        assert manager.plan.filters[0].actions.size() == 0
        assert manager.plan.filters[0].filters.size() == 0
    }

    @Test
    void testCustom() {
        testCriteria("custom { snapshot, allSnapshots -> return snapshot.identifier.contains('a') } ")
    }

    @Test
    void testSourceSpecific() {
        testCriteria('dummyCriteria()')
    }
}
