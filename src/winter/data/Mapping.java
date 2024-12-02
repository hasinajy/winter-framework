package winter.data;

import java.util.Set;

import winter.exceptions.DuplicateMappingException;
import winter.exceptions.InvalidRequestVerbException;

public class Mapping {

    private String className;
    private Set<MappingMethod> mappingMethods;

    /* ------------------------------ Constructors ------------------------------ */
    public Mapping() {
    }

    public Mapping(String className, Set<MappingMethod> mappingMethods) {
        this.setClassName(className);
        this.setMappingMethods(mappingMethods);
    }

    /* --------------------------- Getters and setters -------------------------- */
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Set<MappingMethod> getMappingMethods() {
        return mappingMethods;
    }

    public void setMappingMethods(Set<MappingMethod> mappingMethods) {
        this.mappingMethods = mappingMethods;
    }

    /* --------------------------------- Methods -------------------------------- */
    public MappingMethod getMethod(RequestVerb verb) throws InvalidRequestVerbException {
        for (MappingMethod mappingMethod : this.getMappingMethods()) {
            if (mappingMethod.getVerb() == verb) {
                return mappingMethod;
            }
        }

        throw new InvalidRequestVerbException("Access denied for the specified URL");
    }

    public void addMethod(MappingMethod mappingMethod) throws DuplicateMappingException {
        if (this.getMappingMethods().contains(mappingMethod)) {
            throw new DuplicateMappingException(
                    "Duplicate controller method for the URL '" + mappingMethod.getUrlMapping() + "'");
        }

        this.getMappingMethods().add(mappingMethod);
    }

}
