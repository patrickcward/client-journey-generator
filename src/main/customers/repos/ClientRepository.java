package main.customers.repos;

import main.customers.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {

    void saveClient(Client client);

    Optional<Client> getClientByBusinessName(String businessName);

    List<Client> findAllClients();

    void updateClient(Client client);

    void deleteClient(Client client);
}
