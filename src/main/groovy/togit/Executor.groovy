package togit

import org.codehaus.groovy.control.CompilerConfiguration
import org.slf4j.LoggerFactory
import togit.migration.MigrationManager

class Executor {

    final static LOG = LoggerFactory.getLogger(this.class)

    GroovyShell execute(String[] args) {
        if (!args) {
            LOG.error('Missing migration script parameter')
            System.exit(1)
        }

        File migrationScript = new File(args[0])

        if (!migrationScript.exists() || migrationScript.isDirectory()) {
            LOG.error("Cannot find migration script '$migrationScript'")
            System.exit(1)
        }

        CompilerConfiguration config = new CompilerConfiguration(
            scriptBaseClass:'togit.ScriptBase',
            classpath:migrationScript.absoluteFile.parent
        )

        Binding binding = new Binding(args as String[])
        if (args.length > 1) {
            parseParameters(binding, args[1..-1] as String[])
        }

        MigrationManager.instance.reset()
        GroovyShell shell = new GroovyShell(this.class.classLoader, binding, config)
        shell.evaluate(migrationScript)
        shell
    }

    static void parseParameters(Binding binding, String[] args) {
        args.grep { it.contains('=') }.each {
            String[] split = it.split('=')
            binding.setVariable(split[0], split[1..-1].join('='))
        }
    }
}
