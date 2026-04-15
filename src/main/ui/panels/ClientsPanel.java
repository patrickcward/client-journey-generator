package main.ui.panels;

import main.customers.Client;
import main.customers.services.HibernateClientRepository;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClientsPanel extends JPanel {

    private final HibernateClientRepository clientRepository;

    private final DefaultListModel<String> clientListModel;
    private final JList<String> clientList;

    private final CardLayout detailLayout;
    private final JPanel detailPanel;

    private final Map<String, Client> clientsByUuid;

    public ClientsPanel() {
        this.clientRepository = new HibernateClientRepository();
        this.clientListModel = new DefaultListModel<>();
        this.clientList = new JList<>(clientListModel);
        this.detailLayout = new CardLayout();
        this.detailPanel = new JPanel(detailLayout);
        this.clientsByUuid = new LinkedHashMap<>();

        setLayout(new BorderLayout());
        buildUi();
        loadClientsFromDatabase();
    }

    private void buildUi() {
        JPanel leftPanel = new JPanel(new BorderLayout());

        JButton addClientButton = new JButton("Add Client");
        addClientButton.addActionListener(e -> showAddClientForm());

        leftPanel.add(addClientButton, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(clientList), BorderLayout.CENTER);

        JPanel emptyStatePanel = new JPanel(new BorderLayout());
        emptyStatePanel.add(
                new JLabel("Select a client or add a new one", SwingConstants.CENTER),
                BorderLayout.CENTER
        );

        JPanel addClientFormPanel = buildAddClientFormPanel();

        detailPanel.add(emptyStatePanel, "EMPTY");
        detailPanel.add(addClientFormPanel, "ADD_CLIENT");

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, detailPanel);
        splitPane.setDividerLocation(280);

        add(splitPane, BorderLayout.CENTER);

        detailLayout.show(detailPanel, "EMPTY");

        clientList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedUuid = clientList.getSelectedValue();
                if (selectedUuid != null) {
                    Client client = clientsByUuid.get(selectedUuid);
                    if (client != null) {
                        showClientDetail(client);
                    }
                }
            }
        });

        clientList.setCellRenderer(new DefaultListCellRenderer() {
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
                Client client = clientsByUuid.get(uuid);
                if (client != null) {
                    setText(client.getBusinessName());
                }

                return c;
            }
        });
    }

    private JPanel buildAddClientFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel businessNameLabel = new JLabel("Business Name:");
        JTextField businessNameField = new JTextField(24);
        JButton saveButton = new JButton("Save Client");

        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(businessNameLabel, gbc);

        gbc.gridx = 1;
        form.add(businessNameField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        form.add(saveButton, gbc);

        saveButton.addActionListener(e -> {
            String businessName = businessNameField.getText().trim();

            if (businessName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Business name is required.");
                return;
            }

            try {
                Client client = new Client(businessName);
                clientRepository.saveClient(client);

                businessNameField.setText("");
                loadClientsFromDatabase();
                detailLayout.show(detailPanel, "EMPTY");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to save client: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        panel.add(new JLabel("Add Client", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);

        return panel;
    }

    private void loadClientsFromDatabase() {
        clientListModel.clear();
        clientsByUuid.clear();

        List<Client> clients = clientRepository.findAllClients();

        for (Client client : clients) {
            clientsByUuid.put(client.getUuid(), client);
            clientListModel.addElement(client.getUuid());
        }
    }

    private void showAddClientForm() {
        detailLayout.show(detailPanel, "ADD_CLIENT");
    }

    private void showClientDetail(Client client) {
        JPanel clientDetail = new JPanel(new BorderLayout());

        JLabel title = new JLabel(client.getBusinessName(), SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton deleteButton = new JButton("Delete");
        JButton refreshButton = new JButton("Refresh");

        buttons.add(refreshButton);
        buttons.add(deleteButton);

        JTextArea info = new JTextArea();
        info.setEditable(false);
        info.setText(
                "Business Name: " + client.getBusinessName() + "\n" +
                        "UUID: " + client.getUuid() + "\n" +
                        "Contacts: " + client.getClientContacts().size()
        );

        refreshButton.addActionListener(e -> {
            clientRepository.getClientByBusinessName(client.getBusinessName())
                    .ifPresent(this::showClientDetail);
        });

        deleteButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Delete client '" + client.getBusinessName() + "'?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {
                try {
                    clientRepository.deleteClient(client);
                    loadClientsFromDatabase();
                    detailLayout.show(detailPanel, "EMPTY");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Failed to delete client: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        clientDetail.add(title, BorderLayout.NORTH);
        clientDetail.add(new JScrollPane(info), BorderLayout.CENTER);
        clientDetail.add(buttons, BorderLayout.SOUTH);

        detailPanel.add(clientDetail, "CLIENT_DETAIL");
        detailLayout.show(detailPanel, "CLIENT_DETAIL");
    }
}