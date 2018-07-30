package togit.migration.sources.ccbase.context

import org.slf4j.LoggerFactory
import togit.context.SourceContext
import togit.migration.sources.ccbase.ClearCaseSource

class ClearCaseSourceContext extends SourceContext {
    final static LOG = LoggerFactory.getLogger(this.class)

    ClearCaseSourceContext() {
        source = new ClearCaseSource()
    }

    /**
     * Configures the to the config spec used to establish the views
     * @param path The path of the config spec file
     */
    void configSpec(String path) {
        (source as ClearCaseSource).configSpec = path
    }

    /**
     * Sets the name of the vob to fetch the labels from
     * @param vobName Label vob selector
     */
    void labelVob(String vobName) {
        (source as ClearCaseSource).labelVob = vobName
    }

    /**
     * Sets the name of the file to fetch the labels from
     * @param fileName Label file name
     */
    void labelFile(String fileName) {
        (source as ClearCaseSource).labelFile = fileName
    }

    /**
     * Sets the workspace, i.e. the view path, of the migration
     * @param path The path of the workspace
     */
    void workspace(String path) {
        source.workspace = path
    }

    /**
     * Configures the name for the migration view's tag
     * @param name Name of the migration view's tag
     */
    void viewTag(String name) {
        (source as ClearCaseSource).viewTag = name
    }

    /**
     * Sets the list of vob paths to load when establishing the view
     * @param vobPaths List of vob paths to load
     */
    void vobPaths(List<String> vobPaths) {
        (source as ClearCaseSource).vobPaths = vobPaths
    }
}
