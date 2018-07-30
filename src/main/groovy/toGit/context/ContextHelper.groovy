package toGit.context

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
            if (context instanceof CriteriaContext) context.criteria.clear()
            if (context instanceof ExtractionsContext) context.extractions.clear()
            if (context instanceof ActionsContext) context.actions.clear()

            closure.delegate = context
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
        }
    }
}
