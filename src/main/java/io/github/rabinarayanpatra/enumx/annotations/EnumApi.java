package io.github.rabinarayanpatra.enumx.annotations;

import java.lang.annotation.*;

/**
 * Marks an enum to be exposed as a REST API endpoint.
 * 
 * @author Rabinarayan Patra
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnumApi {
    
    /**
     * The REST endpoint path for this enum.
     * Example: "roles" will create endpoint at /api/roles
     */
    String path();
    
    /**
     * Whether to include all fields by default.
     * If true, all fields with getters are included unless marked with @Hide.
     * If false, only fields marked with @Expose are included.
     */
    boolean includeAllFields() default false;
    
    /**
     * The name of the key field in the response.
     * Default is "key" which will contain the enum name.
     */
    String keyField() default "key";
}
