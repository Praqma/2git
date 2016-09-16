package toGit

import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import toGit.migration.MigrationManager
import toGit.migration.sources.ccucm.CcucmSource
import toGit.migration.targets.git.GitTarget

class Plan_cc2gitBasicTest {
    static String script = $/
    source('ccucm') {

    }

    target('git') {

    }

    migrate {
        before {
            actions {
                custom { println "before" }
            }
        }

        filters {
            filter {
                criteria {
                    baselineName '.*'
                }
                extractions {

                }
                actions {
                    custom { println "act!" }
                }
                filter {
                    // empty
                }
            }
        }

        after {
            actions {
                custom { println "after" }
            }
        }
    }
/$.stripIndent()

    File commandFile

    @Before
    void setup() {
        commandFile = File.createTempFile("cc2git-basic-", ".groovy")
        commandFile.write script
        commandFile.deleteOnExit()
    }

    /**
     * TODO: Prototyping toGit.functional tests. Ignored for now.
     */
    @Test
    @Ignore
    void cc2git_basic() {
        new Executor().execute(commandFile.absolutePath)
        def manager = MigrationManager.instance

        assert manager.source instanceof CcucmSource
        assert manager.targets.values()[0] instanceof GitTarget

        assert manager.plan != null
        assert manager.plan.befores.size() == 1
        assert manager.plan.afters.size() == 1
        assert manager.plan.filters.size() == 1

        assert manager.plan.filters[0].criteria.size() == 1
        assert manager.plan.filters[0].extractions.size() == 1
        assert manager.plan.filters[0].actions.size() == 1
        assert manager.plan.filters[0].filters.size() == 1
    }

    @After
    void cleanup() {

    }
}
