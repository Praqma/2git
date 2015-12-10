package migration.clearcase

import git.Git
@Grab('org.slf4j:slf4j-simple:1.7.7')
import groovy.util.logging.Slf4j
import migration.MigrationOptions
import org.apache.commons.io.FileUtils

@Slf4j
class Component {
    String name     // Component name
    String target   // Target repository path

    List<Stream> streams = []   // Component Streams to migrate
    MigrationOptions migrationOptions   // Some options used in the migration

    /**
     * Component constructor
     * @param name the Component name
     */
    public Component(String name) {
        log.debug('Entering Component().')
        this.name = name
        this.target = "./output/" + name
        log.debug('Exiting Component().')
    }

    /**
     * Sets up a Git repository for the component
     * @return the repository
     */
    File setUpRepository() {
        File repository = new File(target)
        Git.path = repository.path
        if(!repository.exists()) {
            log.info("Path {} does not exist, performing first time setup.", repository.path)
            FileUtils.forceMkdir(new File(repository.path))
            Git.callOrDie('init')
        }
        Git.configureRepository(migrationOptions.gitOptions)
        return repository
    }
}
