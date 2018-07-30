package toGit.context.dummy

import org.junit.Test
import toGit.Executor
import toGit.TestHelper
import toGit.migration.MigrationManager
import toGit.migration.sources.dummy.DummySource
import toGit.migration.targets.dummy.DummyTarget

public class ActionsContextTest {

    //
    // A whole bunch of basic tests
    //

    private static void testAction(String action) {
        def commandFile = TestHelper.createCommandFile(actionScriptFor(action))
        new Executor().execute(commandFile.absolutePath)
        assertPlan()
    }

    static String actionScriptFor(String action) {
        def script = $/
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
        assert manager.plan.filters[0].extractions.size() == 0
        assert manager.plan.filters[0].actions.size() == 1
        assert manager.plan.filters[0].filters.size() == 0
    }

    @Test
    public void testCopy() {
        testAction("copy()")
    }

    @Test
    public void testMove() {
        testAction("move()")
    }

    @Test
    public void testCmd() {
        testAction("cmd 'echo hi'")
    }

    @Test
    public void testCmdWithPath() {
        testAction("cmd 'echo hi', '/usr/anon'")
    }

    @Test
    public void testCustom() {
        testAction("custom { println 'hi' }")
    }

    @Test
    public void testMethodMissing() {
        testAction("foogleburg 'winstonfungler', 5")
    }

    @Test
    public void testFlattenDir() {
        testAction("flattenDir '.', 3")
    }

    //
    // Testing if contexts pass things down sanely
    //

    @Test
    @org.junit.Ignore
    public void closuresPassedDown() {

        def command = $/
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

        def commandFile = TestHelper.createCommandFile(command)
        new Executor().execute(commandFile.absolutePath)
        assertPlan()        
    }
}