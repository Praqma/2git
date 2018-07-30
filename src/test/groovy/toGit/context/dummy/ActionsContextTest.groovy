package togit.context.dummy

import org.junit.Test
import togit.Executor
import togit.TestHelper
import togit.migration.MigrationManager
import togit.migration.sources.dummy.DummySource
import togit.migration.targets.dummy.DummyTarget

class ActionsContextTest {

    //
    // A whole bunch of basic tests
    //

    private static void testAction(String action) {
        new Executor().execute(TestHelper.tempCommandFile(actionScriptFor(action)).absolutePath)
        assertPlan()
    }

    static String actionScriptFor(String action) {
        $/
            source('dummy')
            target('dummy')
            migrate(true) {
                filters {
                    filter {
                        actions {
                            $action
                        }
                    }
                }
            }
        /$.stripIndent()
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
        assert manager.plan.filters[0].extractions.size() == 0
        assert manager.plan.filters[0].actions.size() == 1
        assert manager.plan.filters[0].filters.size() == 0
    }

    @Test
    void testCopy() {
        testAction("copy()")
    }

    @Test
    void testMove() {
        testAction("move()")
    }

    @Test
    void testCmd() {
        testAction("cmd 'echo hi'")
    }

    @Test
    void testCmdWithPath() {
        testAction("cmd 'echo hi', '/usr/anon'")
    }

    @Test
    void testCustom() {
        testAction("custom { println 'hi' }")
    }

    @Test
    void testMethodMissing() {
        testAction("foogleburg 'winstonfungler', 5")
    }

    @Test
    void testFlattenDir() {
        testAction("flattenDir '.', 3")
    }

    //
    // Testing if contexts pass things down sanely
    //

    @Test
    @org.junit.Ignore
    void closuresPassedDown() {
        String command = $/
            source ('dummy')
            target ('dummy')

            def foo = {
                custom {
                    println "foo"
                }
            }

            migrate {
                filters {
                    filter {
                        actions {
                            foo()
                        }
                    }
                }
            }
        /$

        new Executor().execute(TestHelper.tempCommandFile(command))
        assertPlan()        
    }
}
