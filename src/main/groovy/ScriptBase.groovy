import context.MigrationContext
import context.base.Context
import context.base.DslContext
import context.traits.HasSource
import context.traits.HasTarget
import groovy.util.logging.Slf4j
import migration.Migrator
import migration.sources.MigrationSource
import migration.sources.ccucm.Cool
import migration.sources.ccucm.context.CcucmSourceContext
import migration.targets.MigrationTarget
import migration.targets.git.context.GitTargetContext

import static context.ContextHelper.executeInContext

/**
 * Script base for the DSL.
 * The script the user provides is run from this context.
 */
@Slf4j
abstract class ScriptBase extends Script implements Context {

    //TODO dynamically load at one point
    Map<String, HasSource> sources = ['ccucm': new CcucmSourceContext()]
    Map<String, HasTarget> targets = ['git': new GitTargetContext()]

    void source(String identifier, @DslContext(Context) Closure closure) {
        if (!sources.containsKey(identifier)) throw new Exception("Source '$identifier' not supported.")

        // Set the migrator's source
        def sourceContext = sources[identifier]
        executeInContext(closure, sourceContext)
        Migrator.instance.source = sourceContext.source

        // Apply respective traits to the criteria/extraction contexts
        Migrator.instance.criteriaContext = Migrator.instance.source.withCriteria(Migrator.instance.criteriaContext)
        Migrator.instance.extractionsContext = Migrator.instance.source.withExtractions(Migrator.instance.extractionsContext)
    }

    void target(String identifier, @DslContext(Context) Closure closure) {
        if (!targets.containsKey(identifier)) throw new Exception("Target '$identifier' not supported.")

        // Set the migrator's target
        def targetContext = targets[identifier]
        executeInContext(closure, targetContext)
        Migrator.instance.target = targetContext.target

        // Apply respective traits to the action context
        Migrator.instance.actionsContext = Migrator.instance.target.withActions(Migrator.instance.actionsContext)
    }

    /**
     * Closure containing DSL methods used for the migration
     * @param closure The DSL code
     */
    void migrate(@DslContext(MigrationContext) Closure closure) {
        log.debug('Entering migrate().')
        def migrationContext = new MigrationContext()
        executeInContext(closure, migrationContext)
        Migrator.instance.migrate()
        log.info(migrationComplete())
        log.debug('Exiting migrate().')
    }

    /**
     * Outputs stream loadComponents and dependencies to a given log file
     * @param fullyQualifiedStreamName FQ name of the stream to output the dependencies for
     * @param logFileName the File to output to (contents will be YAML)
     */
    void logDependencies(String fullyQualifiedStreamName, String logFileName) {
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
        "\n" +
                "  ______ _____ _   _ _____  _____ _    _ ______ _____  \n" +
                " |  ____|_   _| \\ | |_   _|/ ____| |  | |  ____|  __ \\ \n" +
                " | |__    | | |  \\| | | | | (___ | |__| | |__  | |  | |\n" +
                " |  __|   | | | . ` | | |  \\___ \\|  __  |  __| | |  | |\n" +
                " | |     _| |_| |\\  |_| |_ ____) | |  | | |____| |__| |\n" +
                " |_|    |_____|_| \\_|_____|_____/|_|  |_|______|_____/ \n"
    }

    /**
     * Allows referencing the source as 'source' in the DSL front-end
     * @return the Migrator's MigrationSource
     */
    MigrationSource getSource(){
        return Migrator.instance.source
    }

    /**
     * Allows referencing the target as 'target' in the DSL front-end
     * @return the Migrator's MigrationTarget
     */
    MigrationTarget getTarget(){
        return Migrator.instance.target
    }
}
