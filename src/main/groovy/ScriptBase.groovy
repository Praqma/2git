import dslContext.Context
import dslContext.DslContext
import dslContext.MigrationContext
import groovy.util.logging.Slf4j
import migration.Migrator

import static dslContext.ContextHelper.executeInContext

/**
 * Script base for the DSL.
 * The script the user provides is run from this context.
 */
@Slf4j
abstract class ScriptBase extends Script implements Context {
    /**
     * Closure containing DSL methods used for the migration
     * @param closure The DSL code
     */
    def void migrate(@DslContext(MigrationContext) Closure closure) {
        log.debug('Entering migrate().')
        log.info('Building migration tree.')
        def migrationContext = new MigrationContext()
        executeInContext(closure, migrationContext)
        log.info('Finished building migration tree.')
        Migrator.migrate(migrationContext.vobs);
        log.info(migrationComplete())
        log.debug('Exiting migrate().')
    }

    /**
     * We're done! Weeee!
     * @return The fancy 'Finished' banner to end migration with.
     */
    static def String migrationComplete() {
        "\n" +
                "  ______ _____ _   _ _____  _____ _    _ ______ _____  \n" +
                " |  ____|_   _| \\ | |_   _|/ ____| |  | |  ____|  __ \\ \n" +
                " | |__    | | |  \\| | | | | (___ | |__| | |__  | |  | |\n" +
                " |  __|   | | | . ` | | |  \\___ \\|  __  |  __| | |  | |\n" +
                " | |     _| |_| |\\  |_| |_ ____) | |  | | |____| |__| |\n" +
                " |_|    |_____|_| \\_|_____|_____/|_|  |_|______|_____/ \n"
    }
}
