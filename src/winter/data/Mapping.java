package winter.data;

import java.util.Set;

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

}
