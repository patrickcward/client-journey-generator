package main;

import main.customers.Client;
import main.customers.services.HibernateClientRepository;
import main.ui.MainWindow;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(MainWindow :: new);
    }
}