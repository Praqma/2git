package toGit

import org.codehaus.groovy.control.CompilerConfiguration
import org.slf4j.LoggerFactory
import toGit.migration.MigrationManager

public class Executor {

    final static log = LoggerFactory.getLogger(this.class)

    GroovyShell execute(String[] args) {
        if (!args) {
            log.error("Missing migration script parameter")
            System.exit(1)
        }

        File migrationScript = new File(args[0])

        if (!migrationScript.exists() || migrationScript.isDirectory()) {
            log.error("Cannot find migration script '$migrationScript'")
            System.exit(1)
        }

        def config = new CompilerConfiguration(
                scriptBaseClass: 'toGit.ScriptBase',
                classpath: migrationScript.absoluteFile.parent
        )

        def binding = new Binding(args as String[])
        if (args.length > 1) {
            parseParameters(binding, args[1..-1] as String[])
        }

        MigrationManager.instance.reset()
        def shell = new GroovyShell(this.class.classLoader, binding, config)
        shell.evaluate(migrationScript)
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
