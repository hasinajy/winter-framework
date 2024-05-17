package winter.data;

public class Mapping {
    private String sClass;
    private String sMethod;

    // Constructors
    public Mapping(String sClass, String sMethod) {
        this.setClassName(sClass);
        this.setMethodName(sMethod);
    }

    // Getters & Setters
    public String getClassName() {
        return this.sClass;
    }

    public void setClassName(String sClass) {
        this.sClass = sClass;
    }

    public String getMethodName() {
        return this.sMethod;
    }

    public void setMethodName(String sMethod) {
        this.sMethod = sMethod;
    }
}
