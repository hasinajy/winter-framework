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

public class MappingMethod {
    private Method method;
    private RequestVerb verb;
    private Set<String> auth;

    /* ------------------------------ Constructors ------------------------------ */
    public MappingMethod() {
        this.setAuth(new HashSet<>());
    }

    public MappingMethod(Method method) {
        this();
        this.setMethod(method);
        this.setVerb();
    }

    public MappingMethod(Method method, RequestVerb verb) {
        this();
        this.setMethod(method);
        this.setVerb(verb);
    }

    /* --------------------------- Getters and setters -------------------------- */
    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
        this.setAuth(method);
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

    public Set<String> getAuth() {
        return auth;
    }

    public void setAuth(Set<String> auth) {
        this.auth = auth;
    }

    public void setAuth(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Auth.class)) {
            String[] roles = clazz.getAnnotation(Auth.class).roles();

            for (String role : roles) {
                this.addAuth(role);
            }
        }
    }

    public void setAuth(Method method) {
        if (method.isAnnotationPresent(Auth.class)) {
            String[] roles = method.getAnnotation(Auth.class).roles();

            for (String role : roles) {
                this.addAuth(role);
            }
        }
    }

    /* ----------------------------- Utility methods ---------------------------- */
    public boolean isRest() {
        return this.getMethod().isAnnotationPresent(Rest.class);
    }

    public String getUrlMapping() {
        return this.getMethod().getAnnotation(UrlMapping.class).value();
    }

    public void addAuth(String authString) {
        if (authString != null && !authString.isEmpty()) {
            this.getAuth().add(authString);
        }
    }

    public boolean requiresAuth() {
        return !this.getAuth().isEmpty();
    }

    public boolean hasAuth(String authString) {
        if (!this.requiresAuth()) {
            return true;
        }

        return this.getAuth().contains(authString);
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
