package metier;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import dao.Client;
import exception.DuplicateException;

public class GestionClient implements IGestionClient {

    private EntityManager em;

    public GestionClient() {
        this.em = dao.SingletonConnection.getEntityManager();
    }

    @Override
    public void ajouterClient(Client c) throws DuplicateException {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(c);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            e.printStackTrace();
            throw new DuplicateException("Erreur lors de l'ajout du client");
        }
    }

    @Override
    public List<Client> listerClients() {
        return em.createQuery("SELECT c FROM Client c", Client.class).getResultList();
    }

    @Override
    public List<Client> chercherParNom(String mc) {
        return em.createQuery("SELECT c FROM Client c WHERE c.nom LIKE :mc", Client.class)
                .setParameter("mc", "%" + mc + "%")
                .getResultList();
    }

    @Override
    public void modifierClient(Client c) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(c);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void supprimerClient(int id) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Client c = em.find(Client.class, id);
            if (c != null) {
                em.remove(c);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Client getClient(int id) {
        return em.find(Client.class, id);
    }
}
