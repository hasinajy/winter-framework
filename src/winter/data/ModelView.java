package winter.data;

import java.util.HashMap;

public class ModelView {

    private String jspUrl;
    private HashMap<String, Object> data;

    // Constructors
    public ModelView() {
    }

    public ModelView(String jspUrl) {
        this.setJspUrl(jspUrl);
    }

    // Getters & Setters
    public String getJspUrl() {
        return jspUrl;
    }

    public void setJspUrl(String jspUrl) {
        this.jspUrl = jspUrl;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    // Class methods
    public void addObject(String sAttribute, Object data) {
        this.getData().put(sAttribute, data);
    }

}
