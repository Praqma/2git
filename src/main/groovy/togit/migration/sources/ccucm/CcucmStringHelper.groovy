package togit.migration.sources.ccucm

import java.util.regex.Matcher
import org.slf4j.LoggerFactory

/**
 * A class containing useful String manipulation methods.
 */

class CcucmStringHelper {

    final static LOG = LoggerFactory.getLogger(this.class)

    /**
     * Takes a ClearCase element name and splits it up into identifier, tag and vobName.
     *
     * @param name the ClearCase name (e.g. component:Model@\myVob, stream@\myVob, \myVob, stream)
     * @return a map containing the fqName's identifier, tag and vobName
     */
    static Map<String, String> parseName(String name) {
        String regex = ~/^([^:]*?):?([^:\\]+?)?@?(\\\w+)?$/
        Matcher matcher = name =~ regex
        if (matcher.matches()) {
            Map result = ['identifier':matcher.group(1), 'tag':matcher.group(2), 'vob':matcher.group(3)]
            LOG.debug("Parse result: $result")
            return result
        }
        throw new IllegalArgumentException("Failed to parse name: $name")
    }

    /**
     * Verifies that a String is a proper fully qualified name
     * @param name the String to check
     * @return true if the String is a FQName, otherwise false
     */
    static boolean isSelector(String name) {
        name.matches(/^([\S]*(?=:))?:?([\S]*(?=@))@(\\[\S]*)$/)
    }
}
