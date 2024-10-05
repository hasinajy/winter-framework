package winter.data;

public class Mapping {
    private String sClass;
    private String sMethod;
    private Class<?>[] methodParamTypes = new Class<?>[0];
    private boolean isRest = false;
    private RequestVerb requestVerb = RequestVerb.GET;

    // Constructors
    public Mapping(String sClass, String sMethod) {
        this.setClassName(sClass);
        this.setMethodName(sMethod);
    }

    public Mapping(String sClass, String sMethod, Class<?>[] methodParamTypes) {
        this(sClass, sMethod);
        this.setMethodParamTypes(methodParamTypes);
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

    public Class<?>[] getMethodParamTypes() {
        return this.methodParamTypes;
    }

    public void setMethodParamTypes(Class<?>[] methodParamTypes) {
        this.methodParamTypes = methodParamTypes;
    }

    public boolean getIsRest() {
        return this.isRest;
    }

    public void setIsRest(boolean isRest) {
        this.isRest = isRest;
    }

    public RequestVerb getRequestVerb() {
        return requestVerb;
    }

    public void setRequestVerb(RequestVerb requestVerb) {
        this.requestVerb = requestVerb;
    }
}
