package winter.data;

import java.util.HashSet;
import java.util.Set;

import winter.data.enumdata.RequestVerb;
import winter.data.exception.annotation.DuplicateMappingException;

/**
 * Represents a URL mapping to a set of controller methods in the Winter
 * framework.
 * <p>
 * This class associates a controller class with a collection of
 * {@link MappingMethod}
 * instances, each tied to a specific HTTP verb (e.g., GET, POST). It supports
 * checking
 * for verb availability and adding methods while preventing duplicates.
 * </p>
 *
 * @author Hasina JY
 * @version 1.0.0
 * @since 1.0.0
 */
public class Mapping {

    /** The fully qualified name of the controller class. */
    private String className;

    /** The set of mapping methods associated with this URL. */
    private Set<MappingMethod> mappingMethods;

    /* ------------------------------ Constructors ------------------------------ */

    /**
     * Default constructor initializing an empty set of mapping methods.
     */
    public Mapping() {
        this.setMappingMethods(new HashSet<>());
    }

    /**
     * Constructs a mapping with a specified class name and set of mapping methods.
     *
     * @param className      the fully qualified name of the controller class
     * @param mappingMethods the set of mapping methods for this URL
     */
    public Mapping(String className, Set<MappingMethod> mappingMethods) {
        this.setClassName(className);
        this.setMappingMethods(mappingMethods);
    }

    /* --------------------------- Getters and setters -------------------------- */

    /**
     * Gets the fully qualified name of the controller class.
     *
     * @return the class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the fully qualified name of the controller class.
     *
     * @param className the class name to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Gets the set of mapping methods associated with this URL.
     *
     * @return the set of mapping methods
     */
    public Set<MappingMethod> getMappingMethods() {
        return mappingMethods;
    }

    /**
     * Sets the set of mapping methods associated with this URL.
     *
     * @param mappingMethods the set of mapping methods to set
     */
    public void setMappingMethods(Set<MappingMethod> mappingMethods) {
        this.mappingMethods = mappingMethods;
    }

    /* --------------------------------- Methods -------------------------------- */

    /**
     * Checks if this mapping supports a specific HTTP verb.
     *
     * @param verb the HTTP verb to check (e.g., GET, POST)
     * @return true if a method exists for the verb, false otherwise
     */
    public boolean hasVerb(RequestVerb verb) {
        for (MappingMethod mappingMethod : this.getMappingMethods()) {
            if (mappingMethod.getVerb() == verb) {
                return true;
            }
        }

        return false;
    }

    /**
     * Retrieves the mapping method associated with a specific HTTP verb.
     *
     * @param verb the HTTP verb to find a method for (e.g., GET, POST)
     * @return the matching {@link MappingMethod}, or null if none exists
     */
    public MappingMethod getMethod(RequestVerb verb) {
        for (MappingMethod mappingMethod : this.getMappingMethods()) {
            if (mappingMethod.getVerb() == verb) {
                return mappingMethod;
            }
        }

        return null;
    }

    /**
     * Adds a mapping method to this mapping’s set.
     * <p>
     * Ensures no duplicate methods are added for the same URL and verb combination.
     * </p>
     *
     * @param mappingMethod the mapping method to add
     * @throws DuplicateMappingException if the method duplicates an existing
     *                                   mapping
     */
    public void addMethod(MappingMethod mappingMethod) throws DuplicateMappingException {
        if (this.getMappingMethods().contains(mappingMethod)) {
            throw new DuplicateMappingException(
                    "Duplicate controller method for the URL '" + mappingMethod.getUrlMapping() + "'");
        }

        this.getMappingMethods().add(mappingMethod);
    }
}