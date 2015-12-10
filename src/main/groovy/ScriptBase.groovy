import dslContext.MigrationContext
import groovy.util.logging.Slf4j
import migration.Migrator

/**
 * Script base for the DSL.
 * The script the user provides is run from this context.
 */
@Slf4j
abstract class ScriptBase extends Script {
    /**
     * Closure containing DSL methods used for the migration
     * @param closure The DSL code
     */
    def void migrate(@DelegatesTo(value = MigrationContext, strategy = Closure.DELEGATE_ONLY) Closure closure) {
        log.debug('Entering migrate().')
        def migrationContext = new MigrationContext()
        closure.rehydrate(migrationContext, this, this).run()
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
