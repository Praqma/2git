package toGit.migration.sources.ccm

import toGit.context.base.Context
import toGit.context.traits.SourceContext


class CCMSourceContext implements Context, SourceContext {
    public CCMSourceContext() {
        source = new CCMSource()
    }

    void workspace(String path) {
        source.workspace = path
    }

    void revision(String revision){
        source.revision = revision
    }

    @Deprecated
    void name4part (String name4part){
        namePart(name4part)
    }

    void namePart(String name4part){
        source.name4part = name4part
    }
    
    @Deprecated
    void proj_instance(String proj_instance) {
        projectInstance(proj_instance)
    }

    void projectInstance(String proj_instance){
        source.proj_instance = proj_instance
    }

    @Deprecated
    void ccm_addr (String ccm_addr){
        address(ccm_addr)
    }

    void address(String ccm_addr){
        source.ccm_addr = ccm_addr
    }

    @Deprecated
    void ccm_home(String ccm_home){
        home(ccm_home)
    }

    void home (String ccm_home){
        source.ccm_home = ccm_home
    }

    @Deprecated
    void system_path(String system_path){
        systemPath(system_path)
    }

    void systemPath(String system_path){
        source.system_path = system_path
    }

    void jiraProjectKey(String jiraProjectKey){
        source.jiraProjectKey = jiraProjectKey
    }
}
