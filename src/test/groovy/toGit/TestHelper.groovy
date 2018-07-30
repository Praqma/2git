package togit

class TestHelper {
    static File tempCommandFile(String script) {
        File commandFile = File.createTempFile('command-', '.groovy')
        commandFile.write script
        commandFile.deleteOnExit()
        commandFile
    }
}
