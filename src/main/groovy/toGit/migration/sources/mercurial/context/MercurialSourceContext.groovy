package toGit.migration.sources.mercurial.context

import toGit.context.base.Context
import toGit.context.traits.HasSource
import toGit.migration.sources.mercurial.MercurialSource
import groovy.util.logging.Slf4j

@Slf4j
class MercurialSourceContext implements Context, HasSource {

    public MercurialSourceContext() {
        source = new MercurialSource()
    }

    void branch(String branchName) {
        source.branch = branchName
    }

    void sourceRepo(String absolutePath) {
        source.sourceRepo = absolutePath
        source.repoName = absolutePath.split('/').last()
    }

    void hasSubRepos(boolean b) {
        source.hasSubrepos = b
    }


}
