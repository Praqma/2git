import org.codehaus.groovy.control.CompilerConfiguration

def config = new CompilerConfiguration()
config.scriptBaseClass = 'ScriptBase'

def shell = new GroovyShell(config)

shell.evaluate(new File("command.groovy"))