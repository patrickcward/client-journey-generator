package main.ui.panels;

import javax.swing.*;
import java.awt.*;

public class ClientsPanel extends JPanel {

    private final DefaultListModel<String> clientListModel;
    private final JList<String> clientList;
    private final JPanel detailPanel;
    private final CardLayout detailLayout;

    public ClientsPanel() {
        setLayout(new BorderLayout());

        clientListModel = new DefaultListModel<>();
        clientList = new JList<>(clientListModel);

        detailLayout = new CardLayout();
        detailPanel = new JPanel(detailLayout);

        buildUi();
    }

    private void buildUi() {
        JPanel leftPanel = new JPanel(new BorderLayout());

        JButton addClientButton = new JButton("Add Client");
        addClientButton.addActionListener(e -> showAddClientForm());

        leftPanel.add(addClientButton, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(clientList), BorderLayout.CENTER);

        JPanel emptyStatePanel = new JPanel(new BorderLayout());
        emptyStatePanel.add(new JLabel("Select a client or add a new one", SwingConstants.CENTER), BorderLayout.CENTER);

        JPanel addClientFormPanel = buildAddClientFormPanel();

        detailPanel.add(emptyStatePanel, "EMPTY");
        detailPanel.add(addClientFormPanel, "ADD_CLIENT");

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, detailPanel);
        splitPane.setDividerLocation(250);

        add(splitPane, BorderLayout.CENTER);

        detailLayout.show(detailPanel, "EMPTY");

        clientList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedClient = clientList.getSelectedValue();
                if (selectedClient != null) {
                    showClientDetail(selectedClient);
                }
            }
        });
    }

    private JPanel buildAddClientFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel businessNameLabel = new JLabel("Business Name:");
        JTextField businessNameField = new JTextField(20);
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

            clientListModel.addElement(businessName);
            businessNameField.setText("");
            detailLayout.show(detailPanel, "EMPTY");
        });

        panel.add(new JLabel("Add Client", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);

        return panel;
    }

    private void showAddClientForm() {
        detailLayout.show(detailPanel, "ADD_CLIENT");
    }

    private void showClientDetail(String clientName) {
        JPanel clientDetail = new JPanel(new BorderLayout());
        clientDetail.add(new JLabel("Client: " + clientName, SwingConstants.CENTER), BorderLayout.NORTH);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(new JButton("Edit"));
        buttons.add(new JButton("Delete"));
        buttons.add(new JButton("View Journeys"));

        clientDetail.add(buttons, BorderLayout.CENTER);

        detailPanel.add(clientDetail, "CLIENT_DETAIL");
        detailLayout.show(detailPanel, "CLIENT_DETAIL");
    }
}