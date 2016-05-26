package toGit.utils

import groovy.util.logging.Slf4j
import org.apache.commons.io.FileUtils

@Slf4j
class FileHelper {
    /**
     * Empties the given directory's subdirectories into the given directory, then deletes them.
     * @param directory The directory whose subdirectories to empty/delete
     */
    static void singleFlattenDirectory(File directory) {
        log.info("Flattening directory $directory")
        def subDirectories = directory.listFiles().findAll { it.isDirectory() }
        subDirectories.each { subDirectory ->
            subDirectory.listFiles().each { source ->
                def target = new File(subDirectory.parentFile, source.name)
                if (target.exists()) {
                    log.debug("Deleting $target")
                    if (target.isDirectory()) target.deleteDir()
                    else target.delete()
                }
                log.debug("Moving $source to $target")
                if (source.directory) FileUtils.moveDirectory(source, target)
                else FileUtils.moveFile(source, target)
            }
            subDirectory.deleteDir()
        }
    }
}
