package main.ui.forms;

import main.ui.forms.controllers.FormController;
import main.ui.forms.fields.FormField;

import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Form {

    private final Map<String, FormField> fields;
    private final FormController formController;

    public Form(FormController formController) {
        this.fields = new LinkedHashMap<>();
        this.formController = formController;
    }

    protected void addField(String key, FormField field) {
        fields.put(key, field);
    }

    protected Map<String, FormField> getFields() {
        return fields;
    }

    protected FormField getField(String key) {
        return fields.get(key);
    }

    protected FormController getFormController() {
        return formController;
    }
}