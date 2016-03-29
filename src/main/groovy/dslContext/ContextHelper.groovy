package dslContext

import dslContext.base.Context

class ContextHelper {
    private ContextHelper() {
    }

    static def executeInContext(Closure closure, Context context) {
        if (closure) {
            closure.delegate = context
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.call()
        }
    }
}
