package context.base

import java.lang.annotation.*

/**
 * Marks a {@link Closure} as a context.
 */
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.PARAMETER])
@interface DslContext {
    Class<? extends Context> value()
}
