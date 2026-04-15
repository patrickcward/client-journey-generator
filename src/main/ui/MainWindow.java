package main.ui;

import main.ui.panels.ClientsPanel;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    public MainWindow() {
        setTitle("Client Journey Generator");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTabbedPane(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JToolBar buildToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton clientsButton = new JButton("Clients");
        JButton journeysButton = new JButton("Journeys");
        JButton settingsButton = new JButton("Settings");

        toolBar.add(clientsButton);
        toolBar.add(journeysButton);
        toolBar.add(settingsButton);

        return toolBar;
    }

    private JTabbedPane buildTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.add(new JLabel("Dashboard", SwingConstants.CENTER), BorderLayout.CENTER);

        ClientsPanel clientsPanel = new ClientsPanel();

        JPanel journeysPanel = new JPanel(new BorderLayout());
        journeysPanel.add(new JLabel("Journeys", SwingConstants.CENTER), BorderLayout.CENTER);

        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.addTab("Clients", clientsPanel);
        tabbedPane.addTab("Journeys", journeysPanel);

        return tabbedPane;
    }
}