package winter.data.client;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import winter.data.annotation.http.RequestParam;
import winter.data.exception.annotation.AnnotationNotFoundException;

/**
 * Manages form data extracted from an HTTP request in the Winter framework.
 * <p>
 * This class stores parameter values and error messages for a controller
 * method’s
 * parameters, typically populated from an {@link HttpServletRequest}. It
 * supports validation feedback and data retrieval
 * with optional null formatting.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public class FormData {

    /** The map of parameter names to their values from the request. */
    private Map<String, String> values = new HashMap<>();

    /** The map of parameter names to their associated error messages. */
    private Map<String, String> errorMessages = new HashMap<>();

    /* ------------------------------ Constructors ------------------------------ */

    /**
     * Default constructor for an empty form data object.
     * <p>
     * Initializes empty maps for values and error messages; use setters or the
     * parameterized constructor to populate data.
     * </p>
     */
    public FormData() {
    }

    /**
     * Constructs a form data object from request parameters and an HTTP request.
     * <p>
     * Populates the values map with parameter values from the request and
     * initializes
     * empty error messages, requiring each parameter to have a {@link RequestParam}
     * annotation.
     * </p>
     *
     * @param requestParams the method parameters to extract data for
     * @param req           the HTTP request containing parameter values
     * @throws AnnotationNotFoundException if a parameter lacks a
     *                                     {@link RequestParam} annotation
     */
    public FormData(Parameter[] requestParams, HttpServletRequest req) throws AnnotationNotFoundException {
        for (Parameter param : requestParams) {
            if (!param.isAnnotationPresent(RequestParam.class)) {
                throw new AnnotationNotFoundException(
                        "The annotation @RequestParam was not found on the controller method parameter: "
                                + param.getName());
            }

            String key = param.getAnnotation(RequestParam.class).value();
            this.getValues().put(key, req.getParameter(key));
            this.getErrorMessages().put(key, "");
        }
    }

    /* --------------------------- Getters and setters -------------------------- */

    /**
     * Gets the map of parameter names to their values.
     *
     * @return the values map
     */
    public Map<String, String> getValues() {
        return values;
    }

    /**
     * Sets the map of parameter names to their values.
     *
     * @param values the values map to set
     */
    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    /**
     * Gets the map of parameter names to their error messages.
     *
     * @return the error messages map
     */
    public Map<String, String> getErrorMessages() {
        return errorMessages;
    }

    /**
     * Sets the map of parameter names to their error messages.
     *
     * @param errorMessages the error messages map to set
     */
    public void setErrorMessages(Map<String, String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    /**
     * Gets the value for a specific parameter key, optionally formatting null as an
     * empty string.
     *
     * @param key        the parameter name
     * @param formatNull if true, returns an empty string for null values;
     *                   otherwise, returns null
     * @return the parameter value, or an empty string/null based on formatNull
     */
    public String getValue(String key, boolean formatNull) {
        String value = this.getValues().get(key);
        return (value == null && formatNull) ? "" : value;
    }

    /**
     * Sets the value for a specific parameter key.
     *
     * @param key   the parameter name
     * @param value the value to set
     */
    public void setValue(String key, String value) {
        this.getValues().put(key, value);
    }

    /**
     * Gets the error message for a specific parameter key, optionally formatting
     * null as an empty string.
     *
     * @param key        the parameter name
     * @param formatNull if true, returns an empty string for null messages;
     *                   otherwise, returns null
     * @return the error message, or an empty string/null based on formatNull
     */
    public String getErrorMessage(String key, boolean formatNull) {
        String errorMessage = this.getErrorMessages().get(key);
        return (errorMessage == null && formatNull) ? "" : errorMessage;
    }

    /**
     * Sets the error message for a specific parameter key.
     *
     * @param key   the parameter name
     * @param value the error message to set
     */
    public void setErrorMessage(String key, String value) {
        this.getErrorMessages().put(key, value);
    }
}