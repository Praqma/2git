package migration.sources.mercurial.context

import context.base.Context
import context.traits.HasSource
import groovy.util.logging.Slf4j
import migration.sources.mercurial.MercurialSource

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
        def values = absolutePath.split('/')
        source.repoName = values.last()
    }

    void hasSubRepos(boolean b) {
        source.hasSubrepos = b
    }


}
