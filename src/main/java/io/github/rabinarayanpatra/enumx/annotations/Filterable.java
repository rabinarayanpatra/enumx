package io.github.rabinarayanpatra.enumx.annotations;

import java.lang.annotation.*;

/**
 * Marks a field as filterable in API queries.
 * 
 * @author Rabinarayan Patra
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Filterable {
    
    /**
     * The query parameter name for filtering.
     * If not specified, uses the field name.
     */
    String value() default "";
}