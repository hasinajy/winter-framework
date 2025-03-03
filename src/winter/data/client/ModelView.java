package winter.data.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Represents a model-view combination for rendering views in the Winter
 * framework.
 * <p>
 * This class pairs a JSP URL with a map of data attributes, facilitating the
 * transfer
 * of model data to a view layer. It supports adding objects, converting data to
 * JSON,
 * and setting request attributes for use in JSP rendering.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public class ModelView {

    /** The URL of the JSP file to render. */
    private String jspUrl;

    /** The map of attribute names to their values for the view. */
    private Map<String, Object> data;

    /* ------------------------------ Constructors ------------------------------ */

    /**
     * Default constructor initializing an empty data map.
     * <p>
     * Sets the JSP URL to null and initializes the data map for adding attributes.
     * </p>
     */
    public ModelView() {
        this.setData(new HashMap<>());
    }

    /**
     * Constructs a model-view with a specified JSP URL.
     * <p>
     * Initializes an empty data map and sets the provided JSP URL for rendering.
     * </p>
     *
     * @param jspUrl the URL of the JSP file to render
     */
    public ModelView(String jspUrl) {
        this();
        this.setJspUrl(jspUrl);
    }

    /* --------------------------- Getters & Setters -------------------------- */

    /**
     * Gets the URL of the JSP file to render.
     *
     * @return the JSP URL, or null if not set
     */
    public String getJspUrl() {
        return jspUrl;
    }

    /**
     * Sets the URL of the JSP file to render.
     *
     * @param jspUrl the JSP URL to set
     */
    public void setJspUrl(String jspUrl) {
        this.jspUrl = jspUrl;
    }

    /**
     * Gets the map of attribute names to their values.
     *
     * @return the data map
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * Gets the data map as a JSON string.
     * <p>
     * Uses {@link Gson} to serialize the data map into a JSON-formatted string.
     * </p>
     *
     * @return the JSON representation of the data map
     */
    public String getJsonData() {
        Gson gson = new Gson();
        return gson.toJson(this.getData());
    }

    /**
     * Sets the map of attribute names to their values.
     *
     * @param data the data map to set
     */
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    /* ------------------------------ Class methods ----------------------------- */

    /**
     * Adds an object to the data map.
     * <p>
     * Associates the specified attribute name with the given data object in the
     * map.
     * </p>
     *
     * @param sAttribute the attribute name
     * @param data       the data object to associate
     */
    public void addObject(String sAttribute, Object data) {
        this.getData().put(sAttribute, data);
    }

    /**
     * Sets the data map attributes as request attributes.
     * <p>
     * Transfers all entries from the data map to the {@link HttpServletRequest}
     * as request attributes, making them available for JSP rendering.
     * </p>
     *
     * @param req the HTTP request to set attributes on
     */
    public void setRequestAttributes(HttpServletRequest req) {
        for (Entry<String, Object> entry : this.getData().entrySet()) {
            req.setAttribute(entry.getKey(), entry.getValue());
        }
    }
}