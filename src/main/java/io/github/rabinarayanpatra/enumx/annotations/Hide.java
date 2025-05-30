package io.github.rabinarayanpatra.enumx.annotations;

import java.lang.annotation.*;

/**
 * Marks a field to be excluded from the API response.
 * Takes precedence over includeAllFields=true.
 * 
 * @author Rabinarayan Patra
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Hide {
}