package winter.data;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import winter.data.annotation.Rest;
import winter.data.annotation.http.Auth;
import winter.data.annotation.http.UrlMapping;
import winter.data.annotation.http.requestverb.POST;
import winter.data.enumdata.RequestVerb;

/**
 * Represents a controller method mapped to a specific HTTP verb and
 * authentication roles in the Winter framework.
 * <p>
 * This class encapsulates a {@link Method} object, its associated
 * {@link RequestVerb} (e.g., GET, POST),
 * and a set of authentication roles derived from {@link Auth} annotations. It
 * provides utilities for
 * checking REST status, URL mapping, and authorization requirements.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public class MappingMethod {

    /** The controller method this mapping represents. */
    private Method method;

    /** The HTTP verb associated with this method (e.g., GET, POST). */
    private RequestVerb verb;

    /** The set of authentication roles required to access this method. */
    private Set<String> auth;

    /* ------------------------------ Constructors ------------------------------ */

    /**
     * Default constructor initializing an empty set of authentication roles.
     */
    public MappingMethod() {
        this.setAuth(new HashSet<>());
    }

    /**
     * Constructs a mapping method with a specified method, inferring the verb
     * automatically.
     * <p>
     * Initializes authentication roles as an empty set and sets the verb based on
     * annotations
     * (defaults to GET unless {@link POST} is present).
     * </p>
     *
     * @param method the controller method to map
     */
    public MappingMethod(Method method) {
        this();
        this.setMethod(method);
        this.setVerb();
    }

    /**
     * Constructs a mapping method with a specified method and HTTP verb.
     * <p>
     * Initializes authentication roles as an empty set and sets the provided verb.
     * </p>
     *
     * @param method the controller method to map
     * @param verb   the HTTP verb to associate with the method
     */
    public MappingMethod(Method method, RequestVerb verb) {
        this();
        this.setMethod(method);
        this.setVerb(verb);
    }

    /* --------------------------- Getters and setters -------------------------- */

    /**
     * Gets the controller method this mapping represents.
     *
     * @return the method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Sets the controller method and updates authentication roles accordingly.
     * <p>
     * Extracts roles from the method’s {@link Auth} annotation, if present.
     * </p>
     *
     * @param method the method to set
     */
    public void setMethod(Method method) {
        this.method = method;
        this.setAuth(method);
    }

    /**
     * Gets the HTTP verb associated with this method.
     *
     * @return the verb (e.g., GET, POST)
     */
    public RequestVerb getVerb() {
        return verb;
    }

    /**
     * Sets the HTTP verb associated with this method.
     *
     * @param verb the verb to set
     */
    public void setVerb(RequestVerb verb) {
        this.verb = verb;
    }

    /**
     * Infers and sets the HTTP verb based on method annotations.
     * <p>
     * Sets the verb to {@link RequestVerb#POST} if the {@link POST} annotation is
     * present,
     * otherwise defaults to {@link RequestVerb#GET}.
     * </p>
     */
    private void setVerb() {
        if (this.getMethod().isAnnotationPresent(POST.class)) {
            this.setVerb(RequestVerb.POST);
            return;
        }

        this.setVerb(RequestVerb.GET);
    }

    /**
     * Gets the set of authentication roles required for this method.
     *
     * @return the set of roles
     */
    public Set<String> getAuth() {
        return auth;
    }

    /**
     * Sets the authentication roles for this method.
     *
     * @param auth the set of roles to set
     */
    public void setAuth(Set<String> auth) {
        this.auth = auth;
    }

    /**
     * Sets authentication roles based on a class’s {@link Auth} annotation.
     * <p>
     * Adds all roles specified in the class-level {@link Auth} annotation to the
     * set.
     * </p>
     *
     * @param clazz the class to extract roles from
     */
    public void setAuth(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Auth.class)) {
            String[] roles = clazz.getAnnotation(Auth.class).roles();

            for (String role : roles) {
                this.addAuth(role);
            }
        }
    }

    /**
     * Sets authentication roles based on a method’s {@link Auth} annotation.
     * <p>
     * Adds all roles specified in the method-level {@link Auth} annotation to the
     * set.
     * </p>
     *
     * @param method the method to extract roles from
     */
    public void setAuth(Method method) {
        if (method.isAnnotationPresent(Auth.class)) {
            String[] roles = method.getAnnotation(Auth.class).roles();

            for (String role : roles) {
                this.addAuth(role);
            }
        }
    }

    /* ----------------------------- Utility methods ---------------------------- */

    /**
     * Checks if this method is marked as a REST endpoint.
     *
     * @return true if the method has the {@link Rest} annotation, false otherwise
     */
    public boolean isRest() {
        return this.getMethod().isAnnotationPresent(Rest.class);
    }

    /**
     * Gets the URL mapping value for this method.
     *
     * @return the URL path from the {@link UrlMapping} annotation
     */
    public String getUrlMapping() {
        return this.getMethod().getAnnotation(UrlMapping.class).value();
    }

    /**
     * Adds an authentication role to the set.
     * <p>
     * Ignores null or empty role strings.
     * </p>
     *
     * @param authString the role to add
     */
    public void addAuth(String authString) {
        if (authString != null && !authString.isEmpty()) {
            this.getAuth().add(authString);
        }
    }

    /**
     * Checks if this method requires authentication.
     *
     * @return true if there are any roles in the auth set, false otherwise
     */
    public boolean requiresAuth() {
        return !this.getAuth().isEmpty();
    }

    /**
     * Verifies if a given authentication string satisfies this method’s
     * requirements.
     * <p>
     * Returns true if no authentication is required or if the provided string
     * matches
     * one of the required roles.
     * </p>
     *
     * @param authString the authentication role to check
     * @return true if the role is authorized, false otherwise
     */
    public boolean hasAuth(String authString) {
        if (!this.requiresAuth()) {
            return true;
        }

        return this.getAuth().contains(authString);
    }

    /**
     * Compares this mapping method to another for equality.
     * <p>
     * Two mapping methods are equal if their method names (case-insensitive) and
     * verbs match.
     * </p>
     *
     * @param obj the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MappingMethod)) {
            return false;
        }

        MappingMethod toCompare = (MappingMethod) obj;
        return this.getMethod().getName().equalsIgnoreCase(toCompare.getMethod().getName())
                && this.getVerb() == toCompare.getVerb();
    }

    /**
     * Generates a hash code for this mapping method.
     * <p>
     * Based on the method and verb for consistent use in collections like
     * {@link HashSet}.
     * </p>
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getMethod(), this.getVerb());
    }
}