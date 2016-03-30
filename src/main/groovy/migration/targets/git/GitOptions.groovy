package migration.targets.git

class GitOptions {
    String dir = new File("./output/target/.git").absolutePath
    String workTree = new File("./output/target/tree").absolutePath

    List<String> ignore = ['*.updt', 'lost+found', 'view.dat']
    String user = 'migration'
    String email = 'migration@cctogit.net'

    boolean defaultSetup = true
}
