package winter.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.servlet.http.HttpServletRequest;

public class ModelView {
    private String jspUrl;
    private Map<String, Object> data;

    // Constructors
    public ModelView() {
        this.setData(new HashMap<>());
    }

    public ModelView(String jspUrl) {
        this();
        this.setJspUrl(jspUrl);
    }

    // Getters & Setters
    public String getJspUrl() {
        return jspUrl;
    }

    public void setJspUrl(String jspUrl) {
        this.jspUrl = jspUrl;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    // Class methods
    public void addObject(String sAttribute, Object data) {
        this.getData().put(sAttribute, data);
    }

    public void setRequestAttributes(HttpServletRequest req) {
        for (Entry<String, Object> entry : this.getData().entrySet()) {
            req.setAttribute(entry.getKey(), entry.getValue());
        }
    }
}
