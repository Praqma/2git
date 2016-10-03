package toGit.context

import toGit.context.base.Context
import toGit.context.traits.HasActions
import toGit.context.traits.HasCriteria
import toGit.context.traits.HasExtractions

class ContextHelper {
    private ContextHelper() {
    }

    /**
     * Executes a {@link Closure} in the given {@link Context}
     * @param closure the Closure to execute
     * @param context the Context to run it in
     * @return the result of the Closure
     */
    static def executeInContext(Closure closure, Context context) {
        if (closure) {
            //FIXME Workaround for the CEA arrays not being cleared
            //FIXME This is due to contexts being global and no new instances being made
            if (context instanceof HasCriteria) context.criteria.clear()
            if (context instanceof HasExtractions) context.extractions.clear()
            if (context instanceof HasActions) context.actions.clear()

            closure.delegate = context
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
        }
    }
}
