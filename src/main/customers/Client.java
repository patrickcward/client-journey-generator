package main.customers;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    private String uuid;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Contact> clientContacts = new HashSet<>();

    protected Client() {
        // Required by Hibernate
    }

    public Client(String businessName) {
        this.uuid = UUID.randomUUID().toString();
        this.businessName = businessName;
    }

    public Long getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public Set<Contact> getClientContacts() {
        return clientContacts;
    }

    public void addContact(Contact contact) {
        clientContacts.add(contact);
        contact.setClient(this);
    }

    public void removeContact(Contact contact) {
        clientContacts.remove(contact);
        contact.setClient(null);
    }
}