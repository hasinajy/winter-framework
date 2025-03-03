package winter.data;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Represents a collection of request parameters for a complex object in the
 * Winter framework.
 * <p>
 * This class extracts parameters from an {@link HttpServletRequest} that match
 * a specified prefix,
 * mapping them to field names of a given object type. It is used to populate
 * object instances from
 * form data or query parameters.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public class ObjectRequestParameter {

    /** The class type of the object whose parameters are being extracted. */
    private Class<?> objType;

    /**
     * The prefix used to filter request parameters (e.g., "user" for "user.name").
     */
    private String objPrefix;

    /** The map of field names to their corresponding request parameter values. */
    private Map<String, String> values = new HashMap<>();

    /* ------------------------------ Constructors ------------------------------ */

    /**
     * Constructs an object request parameter from an HTTP request.
     * <p>
     * Initializes the object type, prefix, and extracts parameter values from the
     * request
     * that start with the given prefix, stripping the prefix from the keys.
     * </p>
     *
     * @param objType   the class type of the object
     * @param req       the HTTP request containing parameter data
     * @param objPrefix the prefix for parameter names (e.g., "user" for
     *                  "user.name")
     */
    public ObjectRequestParameter(Class<?> objType, HttpServletRequest req, String objPrefix) {
        this.setObjType(objType);
        this.setObjPrefix(objPrefix);
        this.setValues(req, this.getObjPrefix());
    }

    /* --------------------------- Getters and setters -------------------------- */

    /**
     * Gets the class type of the object whose parameters are being extracted.
     *
     * @return the object type
     */
    public Class<?> getObjType() {
        return objType;
    }

    /**
     * Sets the class type of the object whose parameters are being extracted.
     *
     * @param objType the object type to set
     */
    public void setObjType(Class<?> objType) {
        this.objType = objType;
    }

    /**
     * Gets the prefix used to filter request parameters.
     *
     * @return the prefix (e.g., "user")
     */
    public String getObjPrefix() {
        return objPrefix;
    }

    /**
     * Sets the prefix used to filter request parameters.
     *
     * @param objPrefix the prefix to set
     */
    public void setObjPrefix(String objPrefix) {
        this.objPrefix = objPrefix;
    }

    /**
     * Gets the map of field names to their corresponding request parameter values.
     *
     * @return the map of values
     */
    public Map<String, String> getValues() {
        return values;
    }

    /**
     * Sets the map of field names to their corresponding request parameter values.
     *
     * @param values the map of values to set
     */
    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    /**
     * Extracts and sets parameter values from an HTTP request based on a prefix.
     * <p>
     * Populates the values map with keys stripped of the prefix from the request’s
     * parameter map.
     * Only the first value of multi-valued parameters is used.
     * </p>
     *
     * @param req       the HTTP request containing parameter data
     * @param objPrefix the prefix for parameter names (e.g., "user" for
     *                  "user.name")
     */
    public void setValues(HttpServletRequest req, String objPrefix) {
        req.getParameterMap().forEach((key, value) -> {
            if (key.startsWith(objPrefix)) {
                key = key.substring(objPrefix.length() + 1);
                this.getValues().put(key, value[0]);
            }
        });
    }
}