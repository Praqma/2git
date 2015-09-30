import migration.MigrationContext
import groovy.util.logging.Slf4j
import migration.Migrator

/**
 * Script base for the DSL.
 * The script the user provides is run from this context.
 */
@Slf4j
abstract class ScriptBase extends Script {
    String target = new File('.').getCanonicalPath()    // Path where the Git repositories will be dropped.

    /**
     * Sets the target path for the Git repositories.
     * @param path The path where migrated Git repositories will be dropped.
     */
    def void target(String path) {
        log.debug('Entering target().')
        this.target = path
        log.info('Set migration target to {}', path)
        log.debug('Exiting target().')
    }

    /**
     * Method used for registering elements to migrate.
     * @param path The path where migrated Git repositories will be dropped.
     * @param closure The elements to migrate.
     */
    def void migrate(String path, @DelegatesTo(MigrationContext) Closure closure) {
        target(path)
        migrate(closure)
    }

    /**
     * Method used for registering elements to migrate.
     * @param closure The elements to migrate.
     */
    def void migrate(@DelegatesTo(MigrationContext) Closure closure) {
        log.debug('Entering migrate().')
        def migrationContext = new MigrationContext()
        def migrationContextClosure = closure.rehydrate(migrationContext, this, this)
        migrationContextClosure.resolveStrategy = Closure.DELEGATE_ONLY
        migrationContextClosure()
        Migrator.migrate(migrationContext, this.target);
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
