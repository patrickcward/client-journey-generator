package main.ui.forms;

import main.ui.forms.fields.FieldType;
import main.ui.forms.fields.FormField;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class FormPanel extends JPanel {

    private final Form form;
    private final Map<String, JComponent> inputComponents = new LinkedHashMap<>();

    public FormPanel(Form form) {
        this.form = form;
        setLayout(new GridBagLayout());
        buildForm();
    }

    private void buildForm() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        for (Map.Entry<String, FormField> entry : form.getFields().entrySet()) {
            String key = entry.getKey();
            FormField field = entry.getValue();

            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.3;
            add(new JLabel(field.getLabel()), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;

            JComponent input = createInputComponent(field);
            inputComponents.put(key, input);
            add(input, gbc);

            row++;
        }
    }

    private JComponent createInputComponent(FormField field) {
        if (field.getType() == FieldType.TEXT) {
            JTextField textField = new JTextField();
            Object value = field.getValue();
            if (value != null) {
                textField.setText(value.toString());
            }
            return textField;
        }

        if (field.getType() == FieldType.TEXTAREA) {
            JTextArea textArea = new JTextArea(4, 20);
            Object value = field.getValue();
            if (value != null) {
                textArea.setText(value.toString());
            }
            return new JScrollPane(textArea);
        }

        if (field.getType() == FieldType.CHECKBOX) {
            JCheckBox checkBox = new JCheckBox();
            Object value = field.getValue();
            if (value instanceof Boolean) {
                checkBox.setSelected((Boolean) value);
            }
            return checkBox;
        }

        JTextField fallback = new JTextField();
        Object value = field.getValue();
        if (value != null) {
            fallback.setText(value.toString());
        }
        return fallback;
    }

    public void writeValuesBackToForm() {
        for (Map.Entry<String, JComponent> entry : inputComponents.entrySet()) {
            String key = entry.getKey();
            JComponent component = entry.getValue();
            FormField field = form.getField(key);

            if (component instanceof JTextField textField) {
                field.setValue(textField.getText());
            } else if (component instanceof JCheckBox checkBox) {
                field.setValue(checkBox.isSelected());
            } else if (component instanceof JScrollPane scrollPane) {
                JViewport viewport = scrollPane.getViewport();
                Component view = viewport.getView();
                if (view instanceof JTextArea textArea) {
                    field.setValue(textArea.getText());
                }
            }
        }
    }
}