package winter.data;

import java.util.HashSet;
import java.util.Set;

import winter.data.enumdata.RequestVerb;
import winter.data.exception.annotation.DuplicateMappingException;

public class Mapping {
    private String className;
    private Set<MappingMethod> mappingMethods;

    /* ------------------------------ Constructors ------------------------------ */
    public Mapping() {
        this.setMappingMethods(new HashSet<>());
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
    public boolean hasVerb(RequestVerb verb) {
        for (MappingMethod mappingMethod : this.getMappingMethods()) {
            if (mappingMethod.getVerb() == verb) {
                return true;
            }
        }

        return false;
    }

    public MappingMethod getMethod(RequestVerb verb) {
        for (MappingMethod mappingMethod : this.getMappingMethods()) {
            if (mappingMethod.getVerb() == verb) {
                return mappingMethod;
            }
        }

        return null;
    }

    public void addMethod(MappingMethod mappingMethod) throws DuplicateMappingException {
        if (this.getMappingMethods().contains(mappingMethod)) {
            throw new DuplicateMappingException(
                    "Duplicate controller method for the URL '" + mappingMethod.getUrlMapping() + "'");
        }

        this.getMappingMethods().add(mappingMethod);
    }
}
