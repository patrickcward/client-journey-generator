package main.ui.forms.controllers;

public class FormController implements FormControllerInterface {

    public void submitForm() {
        this.isFormValid();
    }

    @Override
    public void clearForm() {

    }

    @Override
    public Boolean isFormValid() {
        return Boolean.FALSE;
    }
}
