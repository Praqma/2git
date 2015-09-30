package migration

@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
import utils.StringExtensions

@Slf4j
/**
 * Used for registering a Component's Stream for migration
 */
class Stream {
    String name // The name of the Stream
    List<Filter> filters = []

    /**
     * Constructor for Stream
     * @param name The name of the Stream (Doesn't require fully qualified name)
     * @return A new Stream
     */
    def Stream(String name) {
        log.debug('Entering Stream().')
        def parseResult = StringExtensions.parseClearCaseName(name)
        this.name = parseResult.tag
        log.info("Stream {} registered for migration.", this.name)
        log.debug('Exiting Stream().')
    }

    def void migrationSteps(@DelegatesTo(MigrationStepsContext) Closure closure) {
        log.debug('Entering migrationSteps().')
        def stepsContext = new MigrationStepsContext()
        def stepsClosure = closure.rehydrate(stepsContext, this, this)
        stepsClosure.resolveStrategy = Closure.DELEGATE_ONLY
        stepsClosure()
        filters = stepsContext.filters
        log.info('Added {} Steps to Stream {}.', filters.size(), this.name)
        log.debug('Exiting migrationSteps().')
    }
}
