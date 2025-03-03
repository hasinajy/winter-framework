package winter.data.annotation.http.requestverb;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * Marks a method as handling HTTP POST requests in the Winter framework.
 * <p>
 * This annotation indicates that a controller method responds to POST requests,
 * typically used for creating or submitting resources. It is processed by
 * {@link winter.data.MappingMethod} to associate the method with the
 * {@link winter.data.enumdata.RequestVerb#POST} verb in routing logic.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface POST {
}