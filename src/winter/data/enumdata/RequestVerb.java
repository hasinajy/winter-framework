package winter.data.enumdata;

/**
 * Enumerates the HTTP request verbs supported in the Winter framework.
 * <p>
 * This enum defines the standard HTTP methods used for routing and mapping
 * controller
 * methods, such as in {@link winter.data.MappingMethod}. It supports common
 * RESTful
 * operations like retrieving, creating, updating, and deleting resources.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public enum RequestVerb {

    /** Represents an HTTP GET request, typically used to retrieve resources. */
    GET,

    /** Represents an HTTP POST request, typically used to create resources. */
    POST,

    /** Represents an HTTP PUT request, typically used to update resources. */
    PUT,

    /** Represents an HTTP DELETE request, typically used to remove resources. */
    DELETE
}