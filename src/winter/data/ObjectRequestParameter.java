package winter.data;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

public class ObjectRequestParameter {
    private Class<?> objType;
    private Map<String, String> values = new HashMap<>();

    /* ------------------------------ Constructors ------------------------------ */
    public ObjectRequestParameter(Class<?> objType, HttpServletRequest req, String objPrefix) {
        this.setObjType(objType);
        this.setValues(req, objPrefix);
    }

    /* --------------------------- Getters and setters -------------------------- */
    public Class<?> getObjType() {
        return objType;
    }

    public void setObjType(Class<?> objType) {
        this.objType = objType;
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
}
