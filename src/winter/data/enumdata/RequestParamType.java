package winter.data.enumdata;

/**
 * Enumerates the types of request parameters supported in the Winter framework.
 * <p>
 * This enum defines categories for validating or processing request parameters,
 * such as text, email addresses, or numeric values. It is typically used with
 * annotations like {@link winter.data.annotation.http.RequestParam} to enforce
 * parameter constraints.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public enum RequestParamType {

    /** Represents a general text parameter with no specific format constraints. */
    TEXT,

    /**
     * Represents an email address parameter, expected to conform to email format
     * rules.
     */
    EMAIL,

    /**
     * Represents a numeric parameter, expected to contain only numerical values.
     */
    NUMERIC
}