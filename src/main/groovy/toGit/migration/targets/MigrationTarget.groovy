package toGit.migration.targets

import org.slf4j.LoggerFactory
import toGit.context.ActionsContext
import toGit.migration.MigrationManager
import toGit.migration.plan.Action

trait MigrationTarget {

    final static log = LoggerFactory.getLogger(this.class)

    /**
     * The workspace where target contents will be copied to
     */
    String workspace = new File("./output/target").absolutePath

    /**
     * Does any pre-migration setup
     */
    abstract void prepare()

    /**
     * Cleans up any post-migration remains
     */
    abstract void cleanup()

    /**
     * Registers an action with the MigrationManager
     * @param name
     * @param action
     */
    void addAction(String name, Action action) {
        ((ActionsContext) MigrationManager.instance.actionsContext).actions.add(action)
        log.debug("Registered action '$name'")
    }
}