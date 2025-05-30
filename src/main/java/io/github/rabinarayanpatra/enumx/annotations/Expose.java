package io.github.rabinarayanpatra.enumx.annotations;

import java.lang.annotation.*;

/**
 * Marks a field to be included in the API response.
 * Can optionally rename the field in the response.
 * 
 * @author Rabinarayan Patra
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Expose {
    
    /**
     * The name to use in the API response.
     * If not specified, uses the field name.
     */
    String value() default "";
}