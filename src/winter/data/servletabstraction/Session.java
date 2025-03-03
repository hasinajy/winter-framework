package winter.data.servletabstraction;

import jakarta.servlet.http.HttpSession;

/**
 * An abstraction for managing HTTP session attributes in the Winter framework.
 * <p>
 * This class wraps a {@link HttpSession} object, providing a simplified
 * interface to
 * add, delete, update, and retrieve session attributes. It bridges the servlet
 * API
 * with framework-specific logic.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public class Session {

    /** The underlying HTTP session object from the servlet API. */
    private HttpSession httpSession;

    /* ------------------------------ Constructors ------------------------------ */

    /**
     * Default constructor for an empty session object.
     * <p>
     * Initializes the session as null; use the parameterized constructor or setter
     * to associate an {@link HttpSession}.
     * </p>
     */
    public Session() {
    }

    /**
     * Constructs a session object with a specified {@link HttpSession}.
     *
     * @param httpSession the HTTP session to wrap
     */
    public Session(HttpSession httpSession) {
        this.setHttpSession(httpSession);
    }

    /* --------------------------- Getters and setters -------------------------- */

    /**
     * Gets the underlying HTTP session object.
     *
     * @return the wrapped {@link HttpSession}
     */
    public HttpSession getHttpSession() {
        return httpSession;
    }

    /**
     * Sets the underlying HTTP session object.
     *
     * @param httpSession the {@link HttpSession} to wrap
     */
    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    /* ----------------------------- Bridge methods ----------------------------- */

    /**
     * Adds an attribute to the session.
     * <p>
     * Delegates to {@link HttpSession#setAttribute} to store the key-value pair.
     * </p>
     *
     * @param key   the attribute key
     * @param value the attribute value
     */
    public void add(String key, Object value) {
        this.getHttpSession().setAttribute(key, value);
    }

    /**
     * Deletes an attribute from the session.
     * <p>
     * Delegates to {@link HttpSession#removeAttribute} to remove the specified key.
     * </p>
     *
     * @param key the attribute key to remove
     */
    public void delete(String key) {
        this.getHttpSession().removeAttribute(key);
    }

    /**
     * Updates an attribute in the session.
     * <p>
     * Equivalent to {@link #add}, this method sets or overwrites the attribute
     * value
     * for the given key using {@link HttpSession#setAttribute}.
     * </p>
     *
     * @param key   the attribute key
     * @param value the new attribute value
     */
    public void update(String key, Object value) {
        this.add(key, value);
    }

    /**
     * Retrieves an attribute from the session.
     * <p>
     * Delegates to {@link HttpSession#getAttribute} to fetch the value associated
     * with the specified key.
     * </p>
     *
     * @param key the attribute key
     * @return the attribute value, or null if not found
     */
    public Object get(String key) {
        return this.getHttpSession().getAttribute(key);
    }
}