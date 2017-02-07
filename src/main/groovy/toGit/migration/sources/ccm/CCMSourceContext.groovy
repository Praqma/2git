package toGit.migration.sources.ccm

import toGit.context.base.Context
import toGit.context.traits.SourceContext
import toGit.migration.sources.ccm.context.CcmExtractionsContext

class CCMSourceContext implements Context, SourceContext {
    public CCMSourceContext() {
        source = new CCMSource()
    }

    @Override
    Context withExtractions(Context extractionsContext) {
        return extractionsContext as CcmExtractionsContext
    }

    /**
     * Sets the source workspace
     */
    void workspace(String path) {
        source.workspace = path
        //log.debug("Set workspace to $path.")
    }

    void revision (String revision){
        source.revision = revision
    }

    void ccm_addr (String ccm_addr){
        source.ccm_addr = ccm_addr
    }
}
