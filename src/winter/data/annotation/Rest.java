package winter.data.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * Marks a method as a REST endpoint in the Winter framework.
 * <p>
 * This annotation identifies controller methods that operate as RESTful
 * services,
 * returning data in JSON format rather than rendering views. It
 * is processed by {@link winter.data.FrontController} to determine
 * REST behavior.
 * </p>
 *
 * @author Your Name
 * @version 1.0.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Rest {
}