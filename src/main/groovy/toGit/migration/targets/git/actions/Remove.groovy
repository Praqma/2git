package toGit.migration.targets.git.actions

import org.slf4j.LoggerFactory
import toGit.migration.plan.Action

class Remove extends Action {

    final static log = LoggerFactory.getLogger(this.class)

    String path

    public Remove(String path) {
        this.path = path
    }

    @Override
    void act(HashMap<String, Object> extractionMap) {
        log.debug("Removing git repository")
        File wsRoot = new File(path)
        if (wsRoot.directory) wsRoot.deleteDir()
        log.debug("Removed git repository")
    }
}
