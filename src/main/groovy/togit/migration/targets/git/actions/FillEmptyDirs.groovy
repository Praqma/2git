package togit.migration.targets.git.actions

import org.slf4j.LoggerFactory
import togit.migration.plan.Action

class FillEmptyDirs extends Action {

    final static LOG = LoggerFactory.getLogger(this.class)

    String path

    FillEmptyDirs(String path) {
        this.path = path
    }

    @Override
    void act(HashMap<String, Object> extractionMap) {
        LOG.info('Sprinkling dummy files in empty directories')
        sprinkleDummies(new File(path))
        LOG.info('Finished sprinkling dummy files')
    }

    void sprinkleDummies(File directory) {
        List subDirs = directory.listFiles().findAll { it.isDirectory() && it.name != '.git' }
        subDirs.each { subDir ->
            List contents = subDir.listFiles()
            if (contents.any()) {
                sprinkleDummies(subDir)
            } else {
                new File(subDir, '.dummy').createNewFile()
                LOG.info("Dropped .dummy file in $subDir")
            }
        }
    }
}
