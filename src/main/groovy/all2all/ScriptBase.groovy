package all2all

import all2all.context.MigrationContext
import all2all.context.base.Context
import all2all.context.base.DslContext
import all2all.context.traits.HasSource
import all2all.context.traits.HasTarget
import all2all.migration.MigrationManager
import all2all.migration.sources.MigrationSource
import all2all.migration.sources.ccucm.Cool
import all2all.migration.sources.ccucm.context.CcucmSourceContext
import all2all.migration.sources.dummy.DummySourceContext
import all2all.migration.sources.mercurial.context.MercurialSourceContext
import all2all.migration.targets.MigrationTarget
import all2all.migration.targets.dummy.DummyTargetContext
import all2all.migration.targets.git.context.GitTargetContext
import groovy.util.logging.Slf4j

import static all2all.context.ContextHelper.executeInContext

/**
 * Script base for the DSL.
 * The script the user provides is run from this context.
 */
@Slf4j
abstract class ScriptBase extends Script implements Context {

    //TODO dynamically load at one point
    Map<String, HasSource> sources = [
            'dummy'    : new DummySourceContext(),
            'ccucm'    : new CcucmSourceContext(),
            'mercurial': new MercurialSourceContext()
    ]
    Map<String, HasTarget> targets = [
            'dummy': new DummyTargetContext(),
            'git'  : new GitTargetContext()
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
