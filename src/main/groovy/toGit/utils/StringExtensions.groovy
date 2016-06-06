package toGit.utils

import groovy.util.logging.Log

/**
 * A class containing useful String manipulation methods.
 * TODO: Implement as actual extension methods
 */
@Log
class StringExtensions {
    /**
     * Takes a ClearCase element name and splits it up into identifier, tag and vobName.
     *
     * @param name the ClearCase name (e.g. component:Model@\myVob, stream@\myVob, \myVob, stream)
     * @return a map containing the fqName's identifier, tag and vobName
     */
    static Map<String, String> parseClearCaseName(String name) {
        log.fine("Entering parseClearCaseName().")
        def regex = ~/^([^:]*?):?([^:\\]+?)?@?(\\\w+)?$/
        def matcher = name =~ regex
        if (matcher.matches()) {
            def result = ['identifier': matcher.group(1), 'tag': matcher.group(2), 'vob': matcher.group(3)]
            log.fine("Parse result: $result")
            log.fine("Exiting parseClearCaseName().")
            return result
        }
        log.fine("Exiting parseClearCaseName().")
        throw new IllegalArgumentException("Failed to parse name: " + name)
    }

    /**
     * Verifies that a String is a proper fully qualified name
     * @param name the String to check
     * @return true if the String is a FQName, otherwise false
     */
    static boolean isFullyQualifiedName(String name) {
        return name.matches(/^([\S]*(?=:))?:?([\S]*(?=@))@(\\[\S]*)$/)
    }
}
