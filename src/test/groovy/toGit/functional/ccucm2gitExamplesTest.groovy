package toGit.functional

import org.junit.Test
import toGit.Executor
import toGit.TestHelper
import toGit.migration.MigrationManager
import toGit.migration.sources.ccucm.CcucmSource
import toGit.migration.targets.git.GitTarget

class ccucm2gitExamplesTest {

    @Test
    public void ccucm2git(){
        def commandFile = TestHelper.cloneExampleCommandFile("ccucm2git")
        new Executor().execute(commandFile.absolutePath)

        def manager = MigrationManager.instance
        assert manager.source instanceof CcucmSource
        assert manager.target instanceof GitTarget
    }

}
