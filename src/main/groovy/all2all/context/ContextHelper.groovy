package all2all.context

import all2all.context.base.Context
import all2all.context.traits.HasActions
import all2all.context.traits.HasCriteria
import all2all.context.traits.HasExtractions

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
            //TODO This is a workaround for the CEA arrays not being cleared/no new instances of our contexts being made
            if (context instanceof HasCriteria) context.criteria.clear()
            if (context instanceof HasExtractions) context.extractions.clear()
            if (context instanceof HasActions) context.actions.clear()

            closure.delegate = context
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
        }
    }
}
