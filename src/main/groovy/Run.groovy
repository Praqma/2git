@Grab('org.slf4j:slf4j-simple:1.7.13')
@GrabResolver(name = 'praqma', root = 'http://code.praqma.net/repo/maven/', m2Compatible = 'true')
@Grab('net.praqma:cool:0.6.48')
import org.codehaus.groovy.control.CompilerConfiguration

class Run extends Script {
    @Override
    Object run() {
        File commandFile = null
        if(!args) {
            println "ERROR: Missing command file parameter."
            println "Usage: groovy cc2git [command file] [command parameters...]"
            System.exit(1)
        } else {
            commandFile = new File(args[0])
            if(!commandFile.exists() || commandFile.isDirectory()) {
                println "ERROR: Cannot find command file '$commandFile' or command file is a directory."
                System.exit(1)
            }
        }

        def config = new CompilerConfiguration()
        config.scriptBaseClass = 'ScriptBase'
        Binding binding
        if(args.length > 1){
            binding = new Binding(args[1..-1])
            parseParameters(binding, args[1..-1])
        } else
            binding = new Binding()

        def shell = new GroovyShell(binding, config)

        shell.evaluate(commandFile)
    }


    def parseParameters(Binding binding, String[] args) {
        args.each {
            if(!it.contains('=')) return
            def split = it.split('=')
            binding.setVariable(split[0], split[1..-1].join('='))
        }
    }
}