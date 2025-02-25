package winter.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

public class ObjectRequestParameter {
    private Class<?> objType;
    private String[] attrNames;
    private String[] values;

    /* ------------------------------ Constructors ------------------------------ */
    public ObjectRequestParameter(Class<?> objType, String prefix, HttpServletRequest req) {
        this.setObjType(objType);
        this.setAttrNames(prefix, req);
    }

    /* --------------------------- Getters and setters -------------------------- */
    public Class<?> getObjType() {
        return objType;
    }

    public void setObjType(Class<?> objType) {
        this.objType = objType;
    }

    public String[] getAttrNames() {
        return attrNames;
    }

    public void setAttrNames(String[] attrNames) {
        this.attrNames = attrNames;
    }

    public void setAttrNames(String prefix, HttpServletRequest req) {
        List<String> attrNamesList = new ArrayList<>();
        Enumeration<String> paramNames = req.getParameterNames();

        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();

            if (paramName.matches(prefix + ".*")) {
                attrNamesList.add(paramName.split("\\.")[1]);
            }
        }

        this.setAttrNames(attrNamesList.toArray(new String[0]));
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }
}
