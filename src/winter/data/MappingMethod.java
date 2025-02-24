package winter.data;

import java.lang.reflect.Method;
import java.util.Objects;

import winter.annotation.methodlevel.Rest;
import winter.annotation.methodlevel.UrlMapping;
import winter.annotation.verb.POST;
import winter.data.enumdata.RequestVerb;

public class MappingMethod {

    private Method method;
    private RequestVerb verb;

    /* ------------------------------ Constructors ------------------------------ */
    public MappingMethod() {
    }

    public MappingMethod(Method method) {
        this.setMethod(method);
        this.setVerb();
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

    private void setVerb() {
        if (this.getMethod().isAnnotationPresent(POST.class)) {
            this.setVerb(RequestVerb.POST);
            return;
        }

        this.setVerb(RequestVerb.GET);
    }

    /* --------------------------------- Methods -------------------------------- */
    public boolean isRest() {
        return this.getMethod().isAnnotationPresent(Rest.class);
    }

    public String getUrlMapping() {
        return this.getMethod().getAnnotation(UrlMapping.class).value();
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
        return Objects.hash(this.getMethod(), this.getVerb());
    }

}
