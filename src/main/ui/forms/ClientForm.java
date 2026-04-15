package main.ui.forms;

import main.ui.forms.controllers.FormController;
import main.ui.forms.fields.FieldType;
import main.ui.forms.fields.FormField;

public class ClientForm extends Form {

    public ClientForm(FormController formController) {
        super(formController);

        addField(
                "businessName",
                new FormField(
                        "Business Name",
                        "",
                        FieldType.TEXT,
                        true
                )
        );
    }
}