package winter.data.client;

/**
 * A wrapper for a JSON string value in the Winter framework.
 * <p>
 * This class encapsulates a single string representing JSON data,
 * used for
 * handling JSON responses.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public class JsonString {

    /** The JSON string value. */
    private String value;

    /* ------------------------------ Constructors ------------------------------ */

    /**
     * Default constructor for an empty JSON string object.
     * <p>
     * Initializes the value as null; use the parameterized constructor or setter
     * to set a JSON string.
     * </p>
     */
    public JsonString() {
    }

    /**
     * Constructs a JSON string object with a specified value.
     *
     * @param value the JSON string to set
     */
    public JsonString(String value) {
        this.setValue(value);
    }

    /* --------------------------- Getters & Setters -------------------------- */

    /**
     * Gets the JSON string value.
     *
     * @return the JSON string, or null if not set
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Sets the JSON string value.
     *
     * @param value the JSON string to set
     */
    public void setValue(String value) {
        this.value = value;
    }
}