package winter.data.annotation.http;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import winter.data.enumdata.RequestParamType;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * Defines a request parameter for a field or method parameter in the Winter
 * framework.
 * <p>
 * This annotation specifies how a parameter is extracted from an HTTP request,
 * including
 * its name, expected type, and whether it is required. It is applied
 * to controller
 * method parameters or fields and processed by
 * {@link winter.service.ControllerHandler}
 * for data binding and validation.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
public @interface RequestParam {

    /**
     * The name of the request parameter.
     * <p>
     * Specifies the key used to extract the parameter value from the HTTP request.
     * If not provided, defaults to an empty string.
     * </p>
     *
     * @return the parameter name, defaulting to an empty string
     */
    String value() default "";

    /**
     * The expected type of the request parameter.
     * <p>
     * Defines the type constraint for validation (e.g., text, email, numeric),
     * using
     * values from {@link RequestParamType}. Defaults to
     * {@link RequestParamType#TEXT},
     * indicating no specific format constraint.
     * </p>
     *
     * @return the parameter type, defaulting to {@link RequestParamType#TEXT}
     */
    RequestParamType type() default RequestParamType.TEXT;

    /**
     * Indicates whether the parameter is required.
     * <p>
     * If true, the parameter must be present in the request; if false, it is
     * optional.
     * Defaults to false, meaning the parameter can be absent without causing an
     * error.
     * </p>
     *
     * @return true if the parameter is required, false otherwise
     */
    boolean required() default false;
}