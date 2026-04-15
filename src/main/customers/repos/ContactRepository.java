package main.customers.repos;

import main.customers.Contact;

import java.util.List;
import java.util.Optional;

public interface ContactRepository {

    void saveContact(Contact contact);

    Optional<Contact> getContactByUuid(String uuid);

    List<Contact> findAllContacts();

    List<Contact> findContactsByClientUuid(String clientUuid);

    void updateContact(Contact contact);

    void deleteContact(Contact contact);
}