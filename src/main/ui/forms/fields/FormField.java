package main.ui.forms.fields;

public class FormField {

    private final String label;
    private Object value;
    private final FieldType type;
    private final boolean required;

    public FormField(String label, Object value, FieldType type, boolean required) {
        this.label = label;
        this.value = value;
        this.type = type;
        this.required = required;
    }

    public String getLabel() {
        return label;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public FieldType getType() {
        return type;
    }

    public boolean isRequired() {
        return required;
    }
}