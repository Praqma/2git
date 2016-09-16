package toGit

import groovy.util.logging.Log
import toGit.context.MigrationContext
import toGit.context.base.Context
import toGit.context.base.DslContext
import toGit.context.traits.SourceContext
import toGit.context.traits.TargetContext
import toGit.migration.MigrationManager
import toGit.migration.sources.MigrationSource
import toGit.migration.sources.ccucm.Cool
import toGit.migration.sources.ccucm.context.CcucmSourceContext
import toGit.migration.sources.dummy.DummySourceContext
import toGit.migration.targets.MigrationTarget
import toGit.migration.targets.artifactory.context.ArtifactoryTargetContext
import toGit.migration.targets.dummy.DummyTargetContext
import toGit.migration.targets.git.context.GitTargetContext

import static toGit.context.ContextHelper.executeInContext

/**
 * Script base for the DSL.
 * The script the user provides is run from this context.
 */
@Log
abstract class ScriptBase extends Script implements Context {

    //TODO dynamically load at one point
    Map<String, SourceContext> sourceTypes = [
            'dummy': new DummySourceContext(),
            'ccucm': new CcucmSourceContext(),
    ]
    Map<String, TargetContext> targetTypes = [
            'dummy': new DummyTargetContext(),
            'git'  : new GitTargetContext(),
            'artifactory' : new ArtifactoryTargetContext()
    ]

    void source(String type, @DslContext(Context) Closure closure = null) {
        if (!sourceTypes.containsKey(type)) throw new Exception("Source '$type' not supported.")

        // Set the migrator's source
        def sourceContext = sourceTypes[type]
        executeInContext(closure, sourceContext)
        MigrationSource newSource = sourceContext.source
        MigrationManager.instance.source = newSource

        // Apply respective traits to the criteria/extraction contexts
        MigrationManager.instance.criteriaContext = newSource.withCriteria(MigrationManager.instance.criteriaContext)
        MigrationManager.instance.extractionsContext = newSource.withExtractions(MigrationManager.instance.extractionsContext)
    }

    void target(String type, @DslContext(Context) Closure closure = null) {
        target(type, 'default', closure)
    }

    void target(String type, String identifier, @DslContext(Context) Closure closure = null) {
        if (!targetTypes.containsKey(type)) throw new Exception("Target '$type' not supported.")

        // Set the migrator's target
        def targetContext = targetTypes[type]
        executeInContext(closure, targetContext)
        MigrationTarget newTarget = targetContext.target
        MigrationManager.instance.targets[identifier] = targetContext.target

        // Apply respective traits to the action context
        MigrationManager.instance.actionsContext = newTarget.withActions(MigrationManager.instance.actionsContext)
    }

    /**
     * Closure containing DSL methods used for the migration
     * @param closure The DSL code
     */
    void migrate(boolean dryRun = false, @DslContext(MigrationContext) Closure closure) {
        def migrationContext = new MigrationContext()
        executeInContext(closure, migrationContext)
        MigrationManager.instance.migrate(dryRun)
        log.info(migrationComplete())
    }

    /**
     * Outputs stream loadComponents and dependencies to a given simpleLog file
     * @param fullyQualifiedStreamName FQ name of the stream to output the dependencies for
     * @param logFileName the File to output to (contents will be YAML)
     */
    static void logDependencies(String fullyQualifiedStreamName, String logFileName) {
        //TODO move this elsewhere, it's clearcase specific
        def logFile = new File(logFileName)

        if (!logFile.exists())
            logFile.delete()
        if (logFile.parentFile)
            logFile.parentFile.mkdirs()
        logFile.createNewFile()

        logFile.append("components:\n")
        Cool.getModifiableComponentSelectors(fullyQualifiedStreamName).each {
            logFile.append("  - $it\n")
        }

        logFile.append("dependencies:\n")
        Cool.getNonModifiableComponentSelectors(fullyQualifiedStreamName).each {
            logFile.append("  - $it\n")
        }
    }

    /**
     * We're done! Weeee!
     * @return The fancy 'Finished' banner to end migration with.
     */
    static String migrationComplete() {
        def finished = $/
         ______ _____ _   _ _____  _____ _    _ ______ _____
        |  ____|_   _| \ | |_   _|/ ____| |  | |  ____|  __ `.
        | |__    | | |  \| | | | | (___ | |__| | |__  | |  | |
        |  __|   | | | . ` | | |  \___ \|  __  |  __| | |  | |
        | |     _| |_| |\  |_| |_ ____) | |  | | |____| |__| |
        |_|    |_____|_| \_|_____|_____/|_|  |_|______|_____//$
        return finished.stripIndent()
    }

    /**
     * Allows referencing the source as 'source' in the DSL front-end
     * @return the Migrator's MigrationSource
     */
    MigrationSource getSource() {
        return MigrationManager.instance.source
    }

    /**
     * Allows referencing the target as 'target' in the DSL front-end
     * @return the Migrator's first MigrationTarget
     */
    MigrationTarget getTarget() {
        // Kept for backwards compatibility's sake and makes it easier for scripts only using a single target
        return MigrationManager.instance.targets.values()[0]
    }

    /**
     * Allows referencing the targets as 'targets' in the DSL front-end
     * @return the Migrator's MigrationTargets
     */
    LinkedHashMap<String, MigrationTarget> getTargets() {
        // Kept for backwards compatibility's sake and makes it easier for scripts only using a single target
        return MigrationManager.instance.targets
    }
}
