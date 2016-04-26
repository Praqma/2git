package all2all.context;

import all2all.Executor;
import all2all.TestHelper;
import all2all.migration.MigrationManager;
import all2all.migration.sources.dummy.DummySource;
import all2all.migration.targets.dummy.DummyTarget;
import org.junit.Test;


public class CriteriaContextTest {
    private static void testCriteria(String criteria) {
        def commandFile = TestHelper.createCommandFile(actionScriptFor(criteria))
        new Executor().execute(commandFile.absolutePath)
        assertPlan()
    }

    static String actionScriptFor(String criteria) {
        def script = $/
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
        }/$
        return script.stripIndent()
    }

    private static void assertPlan() {
        def manager = MigrationManager.instance
        assert manager.source instanceof DummySource
        assert manager.target instanceof DummyTarget

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
    public void testCustom() throws Exception {
        testCriteria("custom { s -> return s.identifier.contains('a') } ")
    }
}