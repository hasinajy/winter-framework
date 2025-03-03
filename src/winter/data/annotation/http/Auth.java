package winter.data.annotation.http;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * Specifies authentication roles required for a class or method in the Winter
 * framework.
 * <p>
 * This annotation enforces access control by defining roles that must be
 * present to invoke
 * a controller class or method. It can be applied at the class level (for all
 * methods) or
 * method level (for specific endpoints), and is processed by
 * {@link winter.data.MappingMethod} during request handling.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Auth {

    /**
     * The roles required for access.
     * <p>
     * Specifies an array of role names (e.g., "admin", "user") that a user must
     * have to
     * access the annotated class or method. An empty array (default) implies no
     * specific
     * roles are required.
     * </p>
     *
     * @return the array of role names, defaulting to an empty array
     */
    String[] roles() default {};
}