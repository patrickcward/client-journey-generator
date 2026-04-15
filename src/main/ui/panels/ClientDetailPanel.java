package main.ui;

import main.customers.Client;
import main.customers.Contact;
import main.customers.services.HibernateClientRepository;
import main.customers.services.HibernateContactRepository;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClientDetailPanel extends JPanel {

    private final HibernateClientRepository clientRepository;
    private final HibernateContactRepository contactRepository;

    private Client client;

    private final JLabel titleLabel;
    private final JTextArea clientInfoArea;

    private final DefaultListModel<String> contactListModel;
    private final JList<String> contactList;
    private final Map<String, Contact> contactsByUuid;

    private final JTextField nameField;
    private final JTextField emailField;
    private final JTextField phoneField;

    private Contact selectedContact;

    public ClientDetailPanel(Client client) {
        this.clientRepository = new HibernateClientRepository();
        this.contactRepository = new HibernateContactRepository();
        this.client = client;

        this.titleLabel = new JLabel("", SwingConstants.CENTER);
        this.clientInfoArea = new JTextArea();

        this.contactListModel = new DefaultListModel<>();
        this.contactList = new JList<>(contactListModel);
        this.contactsByUuid = new LinkedHashMap<>();

        this.nameField = new JTextField(20);
        this.emailField = new JTextField(20);
        this.phoneField = new JTextField(20);

        setLayout(new BorderLayout());

        buildUi();
        refreshClient();
        loadContacts();
    }

    private void buildUi() {
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));

        clientInfoArea.setEditable(false);
        clientInfoArea.setRows(4);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(new JScrollPane(clientInfoArea), BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        JPanel centrePanel = new JPanel(new GridLayout(1, 2, 12, 12));
        centrePanel.add(buildContactsListPanel());
        centrePanel.add(buildContactFormPanel());

        add(centrePanel, BorderLayout.CENTER);
    }

    private JPanel buildContactsListPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(new JLabel("Contacts"), BorderLayout.NORTH);

        contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                String uuid = (String) value;
                Contact contact = contactsByUuid.get(uuid);
                if (contact != null) {
                    setText(contact.getName() + (contact.getEmail() != null && !contact.getEmail().isBlank()
                            ? " (" + contact.getEmail() + ")"
                            : ""));
                }

                return c;
            }
        });

        contactList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedUuid = contactList.getSelectedValue();
                if (selectedUuid != null) {
                    selectedContact = contactsByUuid.get(selectedUuid);
                    populateContactForm(selectedContact);
                }
            }
        });

        panel.add(new JScrollPane(contactList), BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildContactFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        form.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        form.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(new JLabel("Phone:"), gbc);

        gbc.gridx = 1;
        form.add(phoneField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Contact");
        JButton updateButton = new JButton("Update Contact");
        JButton deleteButton = new JButton("Delete Contact");
        JButton clearButton = new JButton("Clear");

        addButton.addActionListener(e -> addContact());
        updateButton.addActionListener(e -> updateContact());
        deleteButton.addActionListener(e -> deleteContact());
        clearButton.addActionListener(e -> clearContactForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        panel.add(new JLabel("Contact Details"), BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshClient() {
        client = clientRepository.getClientByUuid(client.getUuid()).orElse(client);

        titleLabel.setText(client.getBusinessName());
        clientInfoArea.setText(
                "Business Name: " + client.getBusinessName() + "\n" +
                        "UUID: " + client.getUuid()
        );
    }

    private void loadContacts() {
        contactListModel.clear();
        contactsByUuid.clear();

        List<Contact> contacts = contactRepository.findContactsByClientUuid(client.getUuid());

        for (Contact contact : contacts) {
            contactsByUuid.put(contact.getUuid(), contact);
            contactListModel.addElement(contact.getUuid());
        }
    }

    private void populateContactForm(Contact contact) {
        if (contact == null) {
            return;
        }

        nameField.setText(contact.getName());
        emailField.setText(contact.getEmail() != null ? contact.getEmail() : "");
        phoneField.setText(contact.getPhone() != null ? contact.getPhone() : "");
    }

    private void clearContactForm() {
        selectedContact = null;
        contactList.clearSelection();
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
    }

    private void addContact() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Contact name is required.");
            return;
        }

        try {
            Client managedClient = clientRepository.getClientByUuidWithContacts(client.getUuid())
                    .orElseThrow(() -> new RuntimeException("Client not found"));

            Contact contact = new Contact(
                    name,
                    email.isBlank() ? null : email,
                    phone.isBlank() ? null : phone
            );

            managedClient.addContact(contact);
            clientRepository.updateClient(managedClient);

            refreshClient();
            loadContacts();
            clearContactForm();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to add contact: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateContact() {
        if (selectedContact == null) {
            JOptionPane.showMessageDialog(this, "Please select a contact first.");
            return;
        }

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Contact name is required.");
            return;
        }

        try {
            Contact managedContact = contactRepository.getContactByUuid(selectedContact.getUuid())
                    .orElseThrow(() -> new RuntimeException("Contact not found"));

            managedContact.setName(name);
            managedContact.setEmail(email.isBlank() ? null : email);
            managedContact.setPhone(phone.isBlank() ? null : phone);

            contactRepository.updateContact(managedContact);

            loadContacts();
            clearContactForm();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to update contact: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void deleteContact() {
        if (selectedContact == null) {
            JOptionPane.showMessageDialog(this, "Please select a contact first.");
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "Delete contact '" + selectedContact.getName() + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Contact managedContact = contactRepository.getContactByUuid(selectedContact.getUuid())
                    .orElseThrow(() -> new RuntimeException("Contact not found"));

            contactRepository.deleteContact(managedContact);

            loadContacts();
            clearContactForm();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to delete contact: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}