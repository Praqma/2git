package toGit.context

import org.junit.Test
import toGit.Executor
import toGit.TestHelper
import toGit.migration.MigrationManager
import toGit.migration.sources.dummy.DummySource
import toGit.migration.targets.dummy.DummyTarget

public class ActionsContextTest {

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
    public void testCopy() throws Exception {
        testAction("copy()")
    }

    @Test
    public void testMove() throws Exception {
        testAction("move()")
    }

    @Test
    public void testCmd() throws Exception {
        testAction("cmd 'echo hi'")
    }

    @Test
    public void testCmdWithPath() throws Exception {
        testAction("cmd 'echo hi', '/usr/anon'")
    }

    @Test
    public void testCustom() throws Exception {
        testAction("custom { println 'hi' }")
    }

    @Test
    public void testMethodMissing() throws Exception {
        testAction("foogleburg 'winstonfungler', 5")
    }

    @Test
    public void testFlattenDir() throws Exception {
        testAction("flattenDir '.', 3")
    }
}