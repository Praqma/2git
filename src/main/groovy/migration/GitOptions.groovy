package migration

class GitOptions {
    List<String> ignore = ['*.updt', 'lost+found', 'view.dat']
    String user = 'migration'
    String email = 'migration@cctogit.net'
    String dir = new File("./output/.git").absolutePath
    String workTree = new File("./output/repo").absolutePath
}
