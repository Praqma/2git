package all2all

class TestHelper {
    static File createCommandFile(String script) {
        def commandFile = File.createTempFile("command-", ".groovy")
        commandFile.write script
        commandFile.deleteOnExit()
        return commandFile
    }
}
