package winter.data.client;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import winter.data.annotation.http.RequestParam;
import winter.data.exception.annotation.AnnotationNotFoundException;

public class FormData {
    private Map<String, String> values = new HashMap<>();
    private Map<String, String> errorMessages = new HashMap<>();

    /* ------------------------------ Constructors ------------------------------ */
    public FormData() {
    }

    public FormData(Parameter[] requestParams) throws AnnotationNotFoundException {
        for (Parameter param : requestParams) {
            if (!param.isAnnotationPresent(RequestParam.class)) {
                throw new AnnotationNotFoundException(
                        "The annotation @RequestParam was not found on the controller method parameter: "
                                + param.getName());
            }

            String key = param.getAnnotation(RequestParam.class).name();
            this.getValues().put(key, "");
            this.getErrorMessages().put(key, "");
        }
    }

    /* --------------------------- Getters and setters -------------------------- */
    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    public Map<String, String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(Map<String, String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public String getValue(String key, boolean formatNull) {
        String value = this.getValues().get(key);
        return (value == null && formatNull) ? "" : value;
    }

    public void setValue(String key, String value) {
        this.getValues().put(key, value);
    }

    public String getErrorMessage(String key, boolean formatNull) {
        String errorMessage = this.getErrorMessages().get(key);
        return (errorMessage == null && formatNull) ? "" : errorMessage;
    }

    public void setErrorMessage(String key, String value) {
        this.getErrorMessages().put(key, value);
    }
}
