import org.codehaus.groovy.control.CompilerConfiguration

def config = new CompilerConfiguration()
config.scriptBaseClass = 'ScriptBase'

def shell = new GroovyShell(config)

def envCmd = System.getenv()["CCTOGIT_COMMAND"]
if (envCmd)
    shell.evaluate(envCmd)
else
    shell.evaluate(new File("command.groovy"))