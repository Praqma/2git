package all2all.migration.sources.mercurial

import org.junit.After
import org.junit.Before
import org.junit.Test

public class MercurialRepoTest {

    MercurialRepo repo
    String firstMercurialChangeSet = "184acbc39fb6cdc8f936f6a0ae52be948dba1ceb"
    String lastMercurialChangeSet = "e7c62b086fe80460b9cc650634e2bfa1b5bc24da"
    String firstGitCommit = "02fbc008b0c0bd99686900fc891cedea61c6e49b"
    String lastGitCommit = "29321a9bb60d8dc4c633ef417897376ea7164138"
    String repoPath = "output/source/sourceClone/mercurial2"
    ProcessBuilder builder


    @Before
    public void setUp() throws Exception {
        repo = new MercurialRepo("mercurial2", "mercurial2", "mercurial2", false)
        ProcessBuilder builder = new ProcessBuilder("bash", "-c", "cd $repoPath; hg gclear")
        builder.redirectErrorStream(true)
        builder.start()
    }

    @After
    public void tearDown() throws Exception {
        ProcessBuilder builder = new ProcessBuilder("bash", "-c", "cd $repoPath; hg gclear")
        builder.redirectErrorStream(true)
        builder.start()
    }

    // **************************************** extractRevisionNumbers / merc ****************************************

    //Precondition: the original mercurial repo under migration needs to be cloned in the proper place - output/source/sourceClone/name_of_repo
    @Test
    public void testExtractRevisionNumbersMercArrayLength() {
        repo.extractRevisionNumbers(true)
        assert repo.mercChangesets.size() == 4
    }

    @Test
    public void testExtractRevisionNumbersMercArrayContainsFirst() {
        repo.extractRevisionNumbers(true)
        assert repo.mercChangesets.first().identifier.equals(firstMercurialChangeSet)
    }

    @Test
    public void testExtractRevisionNumbersMercArrayContainsLast() {
        repo.extractRevisionNumbers(true)
        assert repo.mercChangesets.last().identifier.equals(lastMercurialChangeSet)
    }

    // **************************************** extractRevisionNumbers / git ****************************************

    //Precondotion: the original mercurial repo under migration needs to be cloned in the proper place - output/source/sourceClone/name_of_repo
    @Test
    public void testExtractRevisionNumbersGitArrayLength() throws InterruptedException {
        repo.export().waitFor()
        repo.extractRevisionNumbers(false)
        assert repo.gitShas.size() == 4
    }

    @Test
    public void testExtractRevisionNumbersGitArrayContainsFirst() throws InterruptedException {
        repo.export().waitFor()
        repo.extractRevisionNumbers(false)
        assert repo.gitShas.first().identifier.equals(firstGitCommit)
    }

    @Test
    public void testExtractRevisionNumbersGitArrayContainsLast() throws InterruptedException {
        repo.export().waitFor()
        repo.extractRevisionNumbers(false)
        assert repo.gitShas.last().identifier.equals(lastGitCommit)
    }

    // **************************************** Export ****************************************

    //afterClass deletes the generated .git folder
    @Test
    public void testExportFolderFound() throws InterruptedException {
        repo.export().waitFor()
        def gitDir = new File(repoPath + "/.git")
        assert gitDir.exists()
    }

    @Test
    public void testExportHasSameAmountOfCommits() throws InterruptedException {
        repo.export().waitFor()
        repo.extractRevisionNumbers(true)
        repo.extractRevisionNumbers(false)
        assert repo.mercChangesets.size() == repo.gitShas.size()
    }

    @Test
    public void testExportCompareFirstCommitMessage() throws InterruptedException, IOException {
        repo.export().waitFor()

        builder = new ProcessBuilder("bash", "-c", "cd $repoPath; hg log --template '{desc}\\n' -r0")
        builder.redirectErrorStream(true)
        def pr = builder.start()
        def stdInput = new BufferedReader(new InputStreamReader(pr.inputStream))
        def mercMessage = stdInput.readLine()

        builder = new ProcessBuilder("bash", "-c", "cd $repoPath; git log --pretty=%B --reverse | head -1")
        builder.redirectErrorStream(true)
        pr = builder.start()
        stdInput = new BufferedReader(new InputStreamReader(pr.inputStream))
        String gitMessage = stdInput.readLine()
        assert mercMessage.equals(gitMessage)
    }

    @Test
    public void testExportCompareLastCommitMessage() throws InterruptedException, IOException {
        repo.export().waitFor()

        builder = new ProcessBuilder("bash", "-c", "cd $repoPath; hg log --template '{desc}\\n' --limit 1")
        builder.redirectErrorStream(true)
        def pr = builder.start()
        def stdInput = new BufferedReader(new InputStreamReader(pr.getInputStream()))
        def mercMessage = stdInput.readLine()

        builder = new ProcessBuilder("bash", "-c", "cd $repoPath; git log -1 --pretty=%B")
        builder.redirectErrorStream(true)
        pr = builder.start()
        stdInput = new BufferedReader(new InputStreamReader(pr.getInputStream()))
        String gitMessage = stdInput.readLine()

        assert mercMessage.equals(gitMessage)
    }

    // **************************************** setMergedList ****************************************

    @Test
    public void testSetMergedListLength() throws InterruptedException {
        repo.export().waitFor()
        repo.extractRevisionNumbers(true)
        repo.extractRevisionNumbers(false)
        repo.setMergedList()
        assert repo.mercChangesets.size() == 4
    }

    @Test
    public void testSetMergedListFirstsMatch() throws InterruptedException {
        repo.export().waitFor()
        repo.extractRevisionNumbers(true)
        repo.extractRevisionNumbers(false)
        repo.setMergedList()
        def mercFirst = repo.mercChangesets.first().identifier
        def gitFirst = repo.gitShas.first().identifier
        def shouldBeGit = repo.mercGitShas[mercFirst]
        assert gitFirst.equals(shouldBeGit)
    }

    @Test
    public void testSetMergedListLastMatch() throws InterruptedException {
        repo.export().waitFor()
        repo.extractRevisionNumbers(true)
        repo.extractRevisionNumbers(false)
        repo.setMergedList()
        String mercLast = repo.mercChangesets.last().identifier
        String gitLast = repo.gitShas.last().identifier
        String shouldBeGit = repo.mercGitShas[mercLast]
        assert gitLast.equals(shouldBeGit)
    }
}