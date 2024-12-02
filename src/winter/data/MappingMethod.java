package winter.data;

import java.lang.reflect.Method;
import java.util.Objects;

import winter.annotations.Rest;

public class MappingMethod {

    private Method method;
    private RequestVerb verb;

    /* ------------------------------ Constructors ------------------------------ */
    public MappingMethod() {
    }

    public MappingMethod(Method method, RequestVerb verb) {
        this.setMethod(method);
        this.setVerb(verb);
    }

    /* --------------------------- Getters and setters -------------------------- */
    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public RequestVerb getVerb() {
        return verb;
    }

    public void setVerb(RequestVerb verb) {
        this.verb = verb;
    }

    /* --------------------------------- Methods -------------------------------- */
    public boolean isRest() {
        return this.getMethod().isAnnotationPresent(Rest.class);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MappingMethod)) {
            return false;
        }

        MappingMethod toCompare = (MappingMethod) obj;
        return this.getMethod().getName().equalsIgnoreCase(toCompare.getMethod().getName())
                && this.getVerb() == toCompare.getVerb();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this);
    }

}
