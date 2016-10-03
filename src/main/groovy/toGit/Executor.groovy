package toGit

import org.codehaus.groovy.control.CompilerConfiguration
import org.slf4j.LoggerFactory
import toGit.migration.MigrationManager

public class Executor {

    final static log = LoggerFactory.getLogger(this.class)

    GroovyShell execute(String[] args) {
        File commandFile = null
        if (!args) {
            log.error("Missing command file parameter")
            System.exit(1)
        } else {
            commandFile = new File(args[0])
            if (!commandFile.exists() || commandFile.isDirectory()) {
                log.error("Cannot find command file '$commandFile'")
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
