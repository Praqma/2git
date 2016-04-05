package migration.targets.git

class GitOptions {
    List<String> ignore = ['*.updt', 'lost+found', 'view.dat']
    String user = 'migration'
    String email = 'migration@cctogit.net'

    boolean defaultSetup = true
}
