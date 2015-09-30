package migration

@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j

@Slf4j
class MigrationStepsContext {
    List<Filter> filters = []

    def void filter(Closure closure) {
        log.debug('Entering filter().')
        def filter = new Filter()
        def filterClosure = closure.rehydrate(filter, this, this)
        filterClosure.resolveStrategy = Closure.DELEGATE_ONLY
        filterClosure()
        filters.add(filter)
        log.debug('Exiting filter().')
    }
}
