package all2all.migration.sources.mercurial.context

import all2all.context.base.Context
import all2all.context.traits.HasSource
import all2all.migration.sources.mercurial.MercurialSource
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
        def values = absolutePath.split('/')
        source.repoName = values.last()
    }

    void hasSubRepos(boolean b) {
        source.hasSubrepos = b
    }


}
