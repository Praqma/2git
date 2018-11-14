package togit.migration.targets.git.actions

import org.slf4j.LoggerFactory
import togit.migration.plan.Action

class Clear extends Action {

    final static LOG = LoggerFactory.getLogger(this.class)

    String path

    Clear(String path) {
        this.path = path
    }

    @Override
    void act(HashMap<String, Object> extractionMap) {
        LOG.debug('Clearing git repository')
        new File(path).listFiles().findAll { !it.name.startsWith('.git') }.each {
            if (it.directory) {
                it.deleteDir()
            } else {
                it.delete()
            }
        }
        LOG.debug('Cleared git repository')
    }
}
