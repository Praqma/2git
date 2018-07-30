package togit.migration.targets

import org.slf4j.LoggerFactory
import togit.context.ActionsContext
import togit.migration.MigrationManager
import togit.migration.plan.Action

trait MigrationTarget {

    final static LOG = LoggerFactory.getLogger(this.class)

    /**
     * The workspace where target contents will be copied to
     */
    String workspace = new File('./output/target').absolutePath

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
        LOG.debug("Registered action '$name'")
    }
}
