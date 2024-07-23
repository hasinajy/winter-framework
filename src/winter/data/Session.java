package winter.data;

import jakarta.servlet.http.HttpSession;

public class Session {
    private HttpSession httpSession;

    /* ------------------------------ Constructors ------------------------------ */
    public Session() {
    }

    public Session(HttpSession httpSession) {
        this.setHttpSession(httpSession);
    }

    /* --------------------------- Getters and setters -------------------------- */
    public HttpSession getHttpSession() {
        return httpSession;
    }

    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    /* ----------------------------- Bridge methods ----------------------------- */
    public void add(String key, Object value) {
        this.getHttpSession().setAttribute(key, value);
    }

    public void delete(String key) {
        this.getHttpSession().removeAttribute(key);
    }

    public void update(String key, Object value) {
        add(key, value);
    }

    public Object get(String key) {
        return this.getHttpSession().getAttribute(key);
    }
}
