package togit.context.dummy

import org.junit.Test
import togit.Executor
import togit.TestHelper
import togit.migration.MigrationManager
import togit.migration.sources.dummy.DummySource
import togit.migration.targets.dummy.DummyTarget

/* Filters would apply actions to Snapshots that didnt meet criteria. */
class GHI147 {

    private static void runTest() {
        new Executor().execute(TestHelper.tempCommandFile(testScript()).absolutePath)
    }

    static String testScript() {
        """\
        source('dummy')
        target('dummy')
        migrate(false, true) {
            filters {
                filter {
                    criteria {
                        custom { current, all -> current.identifier == "foo" }
                    }
                    actions {
                        custom { println "blip" }
                    }
                }
            }
        }""".stripIndent()
    }

    @Test
    void onlyAppliesToMatchingActions() {
        runTest()
        MigrationManager manager = MigrationManager.instance
        assert manager.plan.steps.size() == 1
        assert manager.plan.steps.foo.actions.size() == 1
    }
}
