package main.customers.services;

import main.customers.Contact;
import main.customers.repos.ContactRepository;
import main.db.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class HibernateContactRepository implements ContactRepository {

    @Override
    public void saveContact(Contact contact) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(contact);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to save contact", e);
        }
    }

    @Override
    public Optional<Contact> getContactByUuid(String uuid) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Contact contact = session.createQuery(
                            "FROM Contact WHERE uuid = :uuid",
                            Contact.class
                    )
                    .setParameter("uuid", uuid)
                    .uniqueResult();

            return Optional.ofNullable(contact);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find contact by uuid", e);
        }
    }

    @Override
    public List<Contact> findAllContacts() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Contact", Contact.class).list();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch all contacts", e);
        }
    }

    @Override
    public List<Contact> findContactsByClientUuid(String clientUuid) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Contact c WHERE c.client.uuid = :clientUuid",
                            Contact.class
                    )
                    .setParameter("clientUuid", clientUuid)
                    .list();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch contacts by client uuid", e);
        }
    }

    @Override
    public void updateContact(Contact contact) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(contact);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to update contact", e);
        }
    }

    @Override
    public void deleteContact(Contact contact) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.remove(session.contains(contact) ? contact : session.merge(contact));

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to delete contact", e);
        }
    }
}