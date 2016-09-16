package toGit.migration.targets.artifactory

import toGit.context.base.Context
import toGit.migration.targets.MigrationTarget
import toGit.migration.targets.artifactory.context.ArtifactoryActionsContext

class ArtifactoryTarget implements MigrationTarget {
    ArtifactoryOptions options

    @Override
    void prepare() {

    }

    @Override
    void cleanup() {

    }

    @Override
    Context withActions(Context actionsContext) {
        return actionsContext as ArtifactoryActionsContext
    }

}
