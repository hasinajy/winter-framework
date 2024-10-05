package winter.data;

public class JsonString {
    private String value;

    // Constructors
    public JsonString() {
    }

    public JsonString(String value) {
        this.setValue(value);
    }

    // Getters & Setters
    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
