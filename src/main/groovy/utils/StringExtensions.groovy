package utils

import groovy.util.logging.Slf4j

/**
 * A class containing useful String manipulation methods.
 * TODO: Implement as actual extension methods
 */
@Slf4j
class StringExtensions {
    /**
     * Takes a ClearCase element name and splits it up into identifier, tag and vob.
     *
     * @param name the ClearCase name (e.g. component:Model@\myVob, stream@\myVob, \myVob, stream)
     * @return a map containing the fqName's identifier, tag and vob
     */
    static Map<String, String> parseClearCaseName(String name) {
        log.debug("Entering parseClearCaseName().")
        def regex = ~/^([^:]*?):?([^:\\]+?)?@?(\\\w+)?$/
        def matcher = name =~ regex
        if (matcher.matches()) {
            def result = ['identifier': matcher.group(1), 'tag': matcher.group(2), 'vob': matcher.group(3)]
            log.debug('Parse result: {}', result)
            log.debug("Exiting parseClearCaseName().")
            return result
        }
        log.debug("Exiting parseClearCaseName().")
        throw new IllegalArgumentException("Failed to parse name: " + name)
    }
}
