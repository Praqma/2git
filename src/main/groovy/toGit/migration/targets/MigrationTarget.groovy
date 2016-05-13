package toGit.migration.targets

import toGit.context.base.Context

trait MigrationTarget {
    /**
     * The workspace where source contents will be copied to
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
     * Adds source-specific contexts to the global ActionsContext
     * @param actionsContext the global ActionsContext
     * @return the global ActionsContext enriched with the source-specific contexts
     */
    abstract Context withActions(Context actionsContext)
}