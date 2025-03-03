package winter.data.annotation.http;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * Defines a URL mapping for a controller class or method in the Winter
 * framework.
 * <p>
 * This annotation specifies the URL path that a controller class or method
 * responds to.
 * It can be applied at the class level (base path for all methods) or method
 * level
 * (specific endpoint), and is processed by
 * {@link winter.service.ControllerScanner}
 * and {@link winter.data.MappingMethod} for routing HTTP requests.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface UrlMapping {

    /**
     * The URL path for the mapping.
     * <p>
     * Specifies the path that this class or method handles (e.g., "/users" or
     * "/getUser").
     * If applied at the class level, it acts as a prefix for method-level mappings.
     * Defaults to an empty string.
     * </p>
     *
     * @return the URL path, defaulting to an empty string
     */
    String value() default "";
}