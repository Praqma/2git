@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1')
import groovyx.net.http.*

def tempDir = "c:/tmp/2git"

source('ccucm') {
    component "myComp@\\vob"
    stream "myStream@\\vob"
}

target('git') {
    user 'johan'
    email 'joa@praqma.net'
}

migrate {
    filters {
        filter {
            criteria {
                afterDate 'dd-MM-yyy', '01-01-2015'
            }
            extractions {
                baselineProperty([myBaselineName: 'shortname'])
            }
            actions {
                copy(source.workspace, tempDir)
                flattenDir(tempDir, 3)
                copy(tempDir, target.workspace)
                emptyDir(tempDir)

                cmd 'git add .', target.workspace
                cmd 'git commit -m "$myBaselineName"', target.workspace
            }
        }
    }
}