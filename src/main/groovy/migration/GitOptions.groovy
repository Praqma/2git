package migration

class GitOptions {
    List<String> ignore = ['*.updt', 'lost+found', 'view.dat']
    String user = 'migration'
    String email = 'migration@cctogit.net'
    String dir = "./output/.git"
    String workTree = "./output/repo"
}
