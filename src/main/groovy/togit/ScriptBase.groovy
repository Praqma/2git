package togit

import static togit.context.ContextHelper.executeInContext

import org.slf4j.LoggerFactory
import toGit.context.MigrationContext
import toGit.context.base.Context
import toGit.context.base.DslContext
import toGit.context.traits.SourceContext
import toGit.context.traits.TargetContext
import toGit.migration.MigrationManager
import toGit.migration.sources.MigrationSource
import toGit.migration.sources.ccbase.context.ClearCaseSourceContext
import toGit.migration.sources.ccucm.context.CcucmSourceContext
import toGit.migration.sources.ccm.CCMSourceContext
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
abstract class ScriptBase extends Script implements Context {

    final static LOG = LoggerFactory.getLogger(this.class)

    // Supported sources
    final Map<String, Class> sourceTypes = [
            'dummy': DummySourceContext,
            'ccucm': CcucmSourceContext,
            'ccm': CCMSourceContext,
            'clearcase': ClearCaseSourceContext,
    ]

    // Supported targets
    final HashMap<String, Class> targetTypes = [
            'dummy':DummyTargetContext,
            'git':GitTargetContext,
            'artifactory':ArtifactoryTargetContext,
    ]

    // Map for storing properties set at runtime
    final runtimeProperties = [:]

    /**
     * If a property that doesn't exist is being set, add it to the runtimeProperties map
     * @param name Name of the property being set
     * @param value Value the property is being set to
     */
    void propertyMissing(String name, Object value) {
        runtimeProperties[name] = value
    }

    /**
     * If a property that doesn't exist is being accessed, try retrieving it from the runtimeProperties map
     * @param name Name of the property being accessed
     * @return the runtimeProperties entry value
     */
    Object propertyMissing(String name) {
        if (!runtimeProperties.containsKey(name)) {
            throw new MissingPropertyException(name, ScriptBase)
        }
        runtimeProperties[name]
    }

    /**
     * Set the source of this migration
     * @param type The source type (e.g. ClearCase UCM)
     * @param closure The configuration closure
     */
    void source(String type, @DslContext(Context) Closure closure = null) {
        if (!sourceTypes.containsKey(type)) {
            throw new NotImplementedException("Source '$type' not supported.")
        }

        // Initialize and configure the source
        SourceContext sourceContext = sourceTypes[type].newInstance()
        executeInContext(closure, sourceContext)
        MigrationSource newSource = sourceContext.source

        // Set MigrationManager's source
        MigrationManager.instance.source = newSource

        // Mix in specific criteria/extraction contexts
        newSource.mixinCriteria()
        newSource.mixinExtractions()
        MigrationManager.instance.refreshContexts()
    }

    /**
     * Set the target of the migration
     * @param type The target type (e.g. Git)
     * @param closure The configuration Closure
     */
    void target(String type, @DslContext(Context) Closure closure = null) {
        target(type, type, closure)
    }

    /**
     * Set the target of the migration
     * @param type The target type (e.g. Git)
     * @param identifier The target name
     * @param closure The configuration Closure
     */
    void target(String type, String identifier, @DslContext(Context) Closure closure = null) {
        if (!targetTypes.containsKey(type)) {
            throw new NotImplementedException("Target '$type' not supported.")
        }

        // Initialize and configure the target
        TargetContext targetContext = targetTypes[type].newInstance() as TargetContext
        executeInContext(closure, targetContext)
        MigrationTarget newTarget = targetContext.target

        // Add to MigrationManager's targets and set runtime property
        MigrationManager.instance.targets[identifier] = newTarget
        this."$identifier" = newTarget
    }

    /**
     * Closure containing DSL methods used for the migration
     * @param closure The DSL code
     */
    static void migrate(boolean skipExecution = false, boolean skipReset = false, @DslContext(MigrationContext) Closure closure) {
        executeInContext(closure, new MigrationContext())
        MigrationManager.instance.migrate(skipExecution)
        LOG.info(migrationComplete())
        if (skipReset) { return }
        MigrationManager.instance.resetMigrationPlan()
    }

    /**
     * We're done! Weeee!
     * @return The fancy 'Finished' banner to end migration with.
     */
    static String migrationComplete() {
        $/        Migration done
         ______ _____ _   _ _____  _____ _    _ ______ _____
        |  ____|_   _| \ | |_   _|/ ____| |  | |  ____|  __ `.
        | |__    | | |  \| | | | | (___ | |__| | |__  | |  | |
        |  __|   | | | . ` | | |  \___ \|  __  |  __| | |  | |
        | |     _| |_| |\  |_| |_ ____) | |  | | |____| |__| |
        |_|    |_____|_| \_|_____|_____/|_|  |_|______|_____//$.stripIndent()
    }

    /**
     * Allows referencing the source as 'source' in the DSL front-end
     * @return the Migrator's MigrationSource
     */
    static MigrationSource getSource() {
        MigrationManager.instance.source
    }

    /**
     * Allows referencing the target as 'target' in the DSL front-end
     * @return the Migrator's first MigrationTarget
     */
    static MigrationTarget getTarget() {
        // Kept for backwards compatibility's sake and makes it easier for scripts only using a single target
        MigrationManager.instance.targets.values()[0]
    }

    /**
     * Allows referencing the targets as 'targets' in the DSL front-end
     * @return the Migrator's MigrationTargets
     */
    static HashMap<String, MigrationTarget> getTargets() {
        MigrationManager.instance.targets
    }
}
