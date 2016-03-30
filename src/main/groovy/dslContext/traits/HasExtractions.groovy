package dslContext.traits

import dslContext.ExtractionsContext
import groovy.util.logging.Slf4j
import migration.filter.extractions.Extraction

import static dslContext.ContextHelper.executeInContext

@Slf4j
trait HasExtractions {
    List<Extraction> extractions = []

    /**
     * Defines extractions
     * @param closure the Extractions configuration
     */
    def void extractions(@DelegatesTo(ExtractionsContext) Closure closure) {
        log.debug('Entering extractions().')
        def extractionsContext = new ExtractionsContext()
        executeInContext(closure, extractionsContext)
        addExtractions(extractionsContext.extractions)
        log.trace('Added {} extractions.', extractionsContext.extractions.size())
        log.debug('Exiting extractions().')
    }

    /**
     * Registers the defined extractions
     * @param given the extractions to register
     */
    def void addExtractions(ArrayList<Extraction> given) {
        extractions.addAll(given)
    }
}
