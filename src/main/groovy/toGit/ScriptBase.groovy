package toGit

import groovy.util.logging.Log
import toGit.context.MigrationContext
import toGit.context.base.Context
import toGit.context.base.DslContext
import toGit.context.traits.HasSource
import toGit.context.traits.HasTarget
import toGit.migration.MigrationManager
import toGit.migration.sources.MigrationSource
import toGit.migration.sources.ccucm.Cool
import toGit.migration.sources.ccucm.context.CcucmSourceContext
import toGit.migration.sources.dummy.DummySourceContext
import toGit.migration.targets.MigrationTarget
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
    Map<String, HasSource> sources = [
            'dummy'    : new DummySourceContext(),
            'ccucm'    : new CcucmSourceContext(),
    ]
    Map<String, HasTarget> targets = [
            'dummy': new DummyTargetContext(),
            'git'  : new GitTargetContext(),
    ]

    void source(String identifier, @DslContext(Context) Closure closure = null) {
        if (!sources.containsKey(identifier)) throw new Exception("Source '$identifier' not supported.")

        // Set the migrator's source
        def sourceContext = sources[identifier]
        executeInContext(closure, sourceContext)
        MigrationManager.instance.source = sourceContext.source

        // Apply respective traits to the criteria/extraction contexts
        MigrationManager.instance.criteriaContext = MigrationManager.instance.source.withCriteria(MigrationManager.instance.criteriaContext)
        MigrationManager.instance.extractionsContext = MigrationManager.instance.source.withExtractions(MigrationManager.instance.extractionsContext)
    }

    void target(String identifier, @DslContext(Context) Closure closure = null) {
        if (!targets.containsKey(identifier)) throw new Exception("Target '$identifier' not supported.")

        // Set the migrator's target
        def targetContext = targets[identifier]
        executeInContext(closure, targetContext)
        MigrationManager.instance.target = targetContext.target

        // Apply respective traits to the action context
        MigrationManager.instance.actionsContext = MigrationManager.instance.target.withActions(MigrationManager.instance.actionsContext)
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
     * Outputs stream loadComponents and dependencies to a given log file
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
     * @return the Migrator's MigrationTarget
     */
    MigrationTarget getTarget() {
        return MigrationManager.instance.target
    }
}
