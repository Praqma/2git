import org.codehaus.groovy.control.CompilerConfiguration

def config = new CompilerConfiguration()
config.scriptBaseClass = 'ScriptBase'
def binding = new Binding(args)
def shell = new GroovyShell(binding, config)
parseParameters(binding, args)

def envCmd = System.getenv()["CCTOGIT_COMMAND"]
if (envCmd)
    shell.evaluate(envCmd)
else
    shell.evaluate(new File("command.groovy"))

def parseParameters(Binding binding, String[] args) {
    args.each {
        if(!it.contains('=')) return
        def split = it.split('=')
        binding.setVariable(split[0], split[1..-1].join('='))
    }
}