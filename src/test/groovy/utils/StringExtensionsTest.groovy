package utils

import spock.lang.Specification

class StringExtensionsTest extends Specification {
    def 'fully qualified name parses'() {
        when: 'I have a correct fully qualified name'
        def selector = 'component:Model@\\2Cool_PVOB'

        then: 'all values should be put under their respective key'
        def parseResult = StringExtensions.parseClearCaseName(selector)
        parseResult.identifier == 'component'
        parseResult.tag == 'Model'
        parseResult.vob == '\\2Cool_PVOB'
    }

    def 'only name parses'() {
        when: 'I have a stream name'
        def selector = 'Server_int'

        then: "the name should end up under the 'tag'"
        def parseResult = StringExtensions.parseClearCaseName(selector)
        !parseResult.identifier
        parseResult.tag == 'Server_int'
        !parseResult.vob
    }

    def 'only vob parses'() {
        when: 'I have a vob name'
        def selector = '\\2Cool'

        then: "the vob name should end up under the 'vob'"
        def parseResult = StringExtensions.parseClearCaseName(selector)
        !parseResult.identifier
        !parseResult.tag
        parseResult.vob == '\\2Cool'
    }
}