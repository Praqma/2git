package toGit

import toGit.migration.MigrationManager
import org.codehaus.groovy.control.CompilerConfiguration

public class Executor {
    GroovyShell execute(String[] args) {
        File commandFile = null
        if (!args) {
            println "ERROR: Missing command file parameter."
            System.exit(1)
        } else {
            commandFile = new File(args[0])
            if (!commandFile.exists() || commandFile.isDirectory()) {
                println "ERROR: Cannot find command file '$commandFile'."
                System.exit(1)
            }
        }

        def scriptDir = new File(getClass().protectionDomain.codeSource.location.path).parent
        def config = new CompilerConfiguration()
        config.setClasspath(scriptDir)
        config.scriptBaseClass = 'toGit.ScriptBase'
        def binding = new Binding(args as String[])
        if (args.length > 1) {
            parseParameters(binding, args[1..-1] as String[])
        }

        MigrationManager.instance.reset()
        def shell = new GroovyShell(binding, config)
        shell.evaluate(commandFile)
        return shell
    }

    static def parseParameters(Binding binding, String[] args) {
        args.each {
            if (!it.contains('=')) return
            def split = it.split('=')
            binding.setVariable(split[0], split[1..-1].join('='))
        }
    }
}
