package winter.data;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import winter.data.client.FormData;

public class ObjectRequestParameter {
    private Class<?> objType;
    private String objPrefix;
    private Map<String, String> values = new HashMap<>();
    private FormData formData;

    /* ------------------------------ Constructors ------------------------------ */
    public ObjectRequestParameter(Class<?> objType, HttpServletRequest req, String objPrefix, FormData formData) {
        this.setObjType(objType);
        this.setObjPrefix(objPrefix);
        this.setValues(req, this.getObjPrefix());
        this.setFormData(formData);
    }

    /* --------------------------- Getters and setters -------------------------- */
    public Class<?> getObjType() {
        return objType;
    }

    public void setObjType(Class<?> objType) {
        this.objType = objType;
    }

    public String getObjPrefix() {
        return objPrefix;
    }

    public void setObjPrefix(String objPrefix) {
        this.objPrefix = objPrefix;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    public void setValues(HttpServletRequest req, String objPrefix) {
        req.getParameterMap().forEach((key, value) -> {
            if (key.startsWith(objPrefix)) {
                key = key.substring(objPrefix.length() + 1);
                this.getValues().put(key, value[0]);
            }
        });
    }

    public FormData getFormData() {
        return formData;
    }

    public void setFormData(FormData formData) {
        this.formData = formData;
    }

    public void setValue(String attrName, String value) {
        this.getFormData().setValue(this.getObjPrefix() + "." + attrName, value);
    }

    public void setErrorMessage(String attrName, String value) {
        this.getFormData().setErrorMessage(this.getObjPrefix() + "." + attrName, value);
    }
}
