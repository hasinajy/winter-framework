package winter.data.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * Marks a class as a controller in the Winter framework.
 * <p>
 * This annotation identifies classes that handle HTTP requests and responses,
 * enabling
 * the framework to scan and register them for routing purposes. It is
 * processed
 * by {@link winter.service.ControllerScanner} during
 * initialization.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Controller {
}