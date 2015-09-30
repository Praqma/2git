package utils

/**
 * A class containing useful String manipulation methods.
 * TODO: Implement as actual extension methods
 */
class StringExtensions {
    /**
     * Takes a ClearCase element name and splits it up into identifier, tag and vob.
     *
     * @param name the ClearCase name (e.g. component:Model@\myVob, stream@\myVob, \myVob, stream)
     * @return a map containing the fqName's identifier, tag and vob
     */
    static Map<String, String> parseClearCaseName(String name) {
        def regex = ~/^([^:]*?):?([^:\\]+?)?@?(\\\w+)?$/
        def matcher = name =~ regex
        if (matcher.matches()) {
            def result = ['identifier': matcher.group(1), 'tag': matcher.group(2), 'vob': matcher.group(3)]
            println 'parseClearCaseName: ' + result
            return result
        }
        throw new IllegalArgumentException("Failed to parse fqName: " + name)
    }
}
