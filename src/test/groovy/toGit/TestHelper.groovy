package toGit

class TestHelper {
    static File cloneExampleCommandFile(String name) {
        def script = this.class.getResource("/examples/${name}.groovy")
        return createCommandFile(script.text)
    }

    static File createCommandFile(String script) {
        def commandFile = File.createTempFile("command-", ".groovy")
        commandFile.write script
        commandFile.deleteOnExit()
        return commandFile
    }
}
