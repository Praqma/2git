package all2all.utils

import org.junit.Test
import spock.lang.Specification

class StringExtensionsTest extends Specification {

    @Test
    def 'fully qualified name parses'() {
        when: 'I have a correct fully qualified name'
        def selector = 'component:Model@\\2Cool_PVOB'

        then: 'all values should be put under their respective key'
        def parseResult = StringExtensions.parseClearCaseName(selector)
        parseResult.identifier == 'component'
        parseResult.tag == 'Model'
        parseResult.vob == '\\2Cool_PVOB'
    }

    @Test
    def 'only name parses'() {
        when: 'I have a stream name'
        def selector = 'Server_int'

        then: "the name should end up under the 'tag'"
        def parseResult = StringExtensions.parseClearCaseName(selector)
        !parseResult.identifier
        parseResult.tag == 'Server_int'
        !parseResult.vob
    }

    @Test
    def 'stream parses'() {
        when: 'I have a stream selector'
        def selector = 'myStream@\\2Cool'

        then: "It should be recognized as a FQName"
        StringExtensions.isFullyQualifiedName(selector)
    }

    @Test
    def 'full stream parses'() {
        when: 'I have a stream selector'
        def selector = 'stream:myStream@\\2Cool'

        then: "It should be recognized as a FQName"
        StringExtensions.isFullyQualifiedName(selector)
    }

    @Test
    def 'component parses'() {
        when: 'I have a component selector'
        def selector = 'myComponent@\\2Cool'

        then: "It should be recognized as a FQName"
        StringExtensions.isFullyQualifiedName(selector)
    }

    @Test
    def 'full component parses'() {
        when: 'I have a component selector'
        def selector = 'component:myComponent@\\2Cool'

        then: "It should be recognized as a FQName"
        StringExtensions.isFullyQualifiedName(selector)
    }
}