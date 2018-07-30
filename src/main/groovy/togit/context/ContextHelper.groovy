package togit.context

class ContextHelper {
    private ContextHelper() {
    }

    /**
     * Executes a {@link Closure} in the given {@link Context}
     * @param closure the Closure to execute
     * @param context the Context to run it in
     * @return the result of the Closure
     */
    static Object executeInContext(Closure closure, Context context) {
        if (!closure) {
            return
        }

        //FIXME Due to contexts being global,
        //FIXME multiple migration blocks in a single command script interfere with eachother.
        //FIXME This is a workaround to allow for multiple migration blocks.
        switch (context.class) {
            case CriteriaContext:
                context.criteria.clear()
                break
            case ExtractionsContext:
                context.extractions.clear()
                break
            case ActionsContext:
                context.actions.clear()
                break
        }

        closure.delegate = context
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
    }
}
