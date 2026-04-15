package main.customers.services;

import main.customers.Client;
import main.customers.repos.ClientRepository;
import main.db.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class HibernateClientRepository implements ClientRepository {

    @Override
    public void saveClient(Client client) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(client);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to save client", e);
        }
    }

    @Override
    public Optional<Client> getClientByBusinessName(String businessName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Client client = session.createQuery(
                            "FROM Client WHERE businessName = :businessName",
                            Client.class
                    )
                    .setParameter("businessName", businessName)
                    .uniqueResult();

            return Optional.ofNullable(client);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find client by business name", e);
        }
    }

    @Override
    public List<Client> findAllClients() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Client", Client.class).list();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch all clients", e);
        }
    }

    @Override
    public void deleteClient(Client client) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.remove(session.contains(client) ? client : session.merge(client));

            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            throw new RuntimeException("Failed to delete client", e);
        }
    }

    @Override
    public void updateClient(Client client) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(client);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to update client", e);
        }
    }
}