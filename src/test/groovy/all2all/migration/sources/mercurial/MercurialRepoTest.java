package all2all.migration.sources.mercurial;

import all2all.migration.plan.Snapshot;
import org.junit.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static org.junit.Assert.*;


public class MercurialRepoTest {

    MercurialRepo repo;
    String firstMercurialChangeSet = "184acbc39fb6cdc8f936f6a0ae52be948dba1ceb";
    String lastMercurialChangeSet = "e7c62b086fe80460b9cc650634e2bfa1b5bc24da";
    String firstGitCommit = "02fbc008b0c0bd99686900fc891cedea61c6e49b";
    String lastGitCommit = "29321a9bb60d8dc4c633ef417897376ea7164138";
    String repoPath = "output/source/sourceClone/mercurial2";
    ProcessBuilder builder;
    Process pr;



    @Before
    public void setUp() throws Exception {
        repo = new MercurialRepo("/mercurial2", "mercurial2");
        ProcessBuilder builder = new ProcessBuilder(
                "bash", "-c", "cd "+ repoPath + "; hg gclear" );
        builder.redirectErrorStream(true);
        Process pr = builder.start();
    }

    @After
    public void tearDown() throws Exception {
        ProcessBuilder builder = new ProcessBuilder(
                "bash", "-c", "cd "+ repoPath + "; hg gclear");
        builder.redirectErrorStream(true);
        Process pr = builder.start();
    }

    // **************************************** extractRevisionNumbers / merc ****************************************

    //Precondition: the original mercurial repo under migration needs to be cloned in the proper place - output/source/sourceClone/name_of_repo
    @Test
    public void testExtractRevisionNumbersMercArrayLength()  {
        repo.extractRevisionNumbers(true);
        assertEquals("Length of the mercCharset Array is ", 4, repo.getMercChangeSets().size());
    }

    @Test
    public void testExtractRevisionNumbersMercArrayContainsFirst() {
        repo.extractRevisionNumbers(true);
        List<Snapshot> temp = repo.getMercChangeSets();
        assertEquals("First item of the mercCharset Array is ", firstMercurialChangeSet , temp.get(0).getIdentifier());
    }

    @Test
    public void testExtractRevisionNumbersMercArrayContainsLast() {
        repo.extractRevisionNumbers(true);
        List<Snapshot> temp = repo.getMercChangeSets();
        assertEquals("First item of the mercCharset Array is ", lastMercurialChangeSet , temp.get(temp.size() - 1).getIdentifier());
    }

    // **************************************** extractRevisionNumbers / git ****************************************

    //Precondotion: the original mercurial repo under migration needs to be cloned in the proper place - output/source/sourceClone/name_of_repo
    @Test
    public void testExtractRevisionNumbersGitArrayLength() throws InterruptedException {
        repo.export().waitFor();
        repo.extractRevisionNumbers(false);
        assertEquals("Length of the gitShas Array is ", 4, repo.getGitShas().size());
    }

    @Test
    public void testExtractRevisionNumbersGitArrayContainsFirst() throws InterruptedException {
        repo.export().waitFor();
        repo.extractRevisionNumbers(false);
        List<Snapshot> temp = repo.getGitShas();
        assertEquals("First item of the gitShas Array is ", firstGitCommit , temp.get(0).getIdentifier());
    }

    @Test
    public void testExtractRevisionNumbersGitArrayContainsLast() throws InterruptedException {
        repo.export().waitFor();
        repo.extractRevisionNumbers(false);
        List<Snapshot> temp = repo.getGitShas();
        assertEquals("First item of the gitShas Array is ", lastGitCommit , temp.get(temp.size() - 1).getIdentifier());
    }

    // **************************************** Export ****************************************

    //afterClass deletes the generated .git folder
    @Test
    public void testExportFolderFound() throws InterruptedException {
        repo.export().waitFor();
        File git = new File(repoPath +"/.git");
        assertTrue("Check if the .git folder has been created", git.exists());
    }

    @Test
    public void testExportHasSameAmountOfCommits() throws InterruptedException {
        repo.export().waitFor();
        repo.extractRevisionNumbers(true);
        repo.extractRevisionNumbers(false);
        int mercCommits = repo.getMercChangeSets().size();
        int gitCommits = repo.getGitShas().size();
        assertEquals("Equal numbers of commits expected", mercCommits, gitCommits);
    }

    @Test
    public void testExportCompareFirstCommitMessage() throws InterruptedException, IOException {
        repo.export().waitFor();
        builder = new ProcessBuilder(
                "bash", "-c", "cd "+ repoPath + "; hg log --template '{desc}\\n' -r0" );
        builder.redirectErrorStream(true);
        pr = builder.start();
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String mercMessage = stdInput.readLine();
        builder = new ProcessBuilder(
                "bash", "-c", "cd "+ repoPath + "; git log --pretty=%B --reverse | head -1" );
        builder.redirectErrorStream(true);
        pr = builder.start();
        stdInput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String gitMessage = stdInput.readLine();
        assertEquals(mercMessage, gitMessage);
    }

    @Test
    public void testExportCompareLastCommitMessage() throws InterruptedException, IOException {
        repo.export().waitFor();
        builder = new ProcessBuilder(
                "bash", "-c", "cd "+ repoPath + "; hg log --template '{desc}\\n' --limit 1" );
        builder.redirectErrorStream(true);
        pr = builder.start();
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String mercMessage = stdInput.readLine();
        builder = new ProcessBuilder(
                "bash", "-c", "cd "+ repoPath + "; git log -1 --pretty=%B" );
        builder.redirectErrorStream(true);
        pr = builder.start();
        stdInput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String gitMessage = stdInput.readLine();
        assertEquals(mercMessage, gitMessage);
    }

    // **************************************** setMergedList ****************************************

    @Test
    public void testSetMergedListLength() throws InterruptedException {
        repo.export().waitFor();
        repo.extractRevisionNumbers(true);
        repo.extractRevisionNumbers(false);
        repo.setMergedList();
        assertEquals("Lenght of map", 4, repo.getMercGitShas().size());;
    }

    @Test
    public void testSetMergedListFirstsMatch() throws InterruptedException {
        repo.export().waitFor();
        repo.extractRevisionNumbers(true);
        repo.extractRevisionNumbers(false);
        repo.setMergedList();
        String mercFirst = repo.getMercChangeSets().get(0).getIdentifier();
        String gitFirst = repo.getGitShas().get(0).getIdentifier();
        String shouldBeGit = repo.getMercGitShas().get(mercFirst);
        assertEquals("Matching first shas", gitFirst, shouldBeGit);;
    }

    @Test
    public void testSetMergedListLastMatch() throws InterruptedException {
        repo.export().waitFor();
        repo.extractRevisionNumbers(true);
        repo.extractRevisionNumbers(false);
        repo.setMergedList();
        int size = repo.getMercChangeSets().size();
        String mercLast = repo.getMercChangeSets().get(size-1).getIdentifier();
        String gitLast = repo.getGitShas().get(size-1).getIdentifier();
        String shouldBeGit = repo.getMercGitShas().get(mercLast);
        assertEquals("Matching first shas", gitLast, shouldBeGit);;
    }
}