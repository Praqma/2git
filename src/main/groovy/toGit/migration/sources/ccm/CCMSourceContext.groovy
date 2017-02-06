package toGit.migration.sources.ccm

import toGit.context.base.Context
import toGit.context.traits.SourceContext

class CCMSourceContext implements Context, SourceContext {
    public CCMSourceContext() {
        source = new CCMSource()
    }

    void revision (String revision){
        source.revision = revision
    }
}
