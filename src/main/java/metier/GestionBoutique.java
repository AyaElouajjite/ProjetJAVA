package metier;

import java.util.List;
import javax.persistence.EntityManager;

import dao.Boutique;
import dao.Proprietaire;

public class GestionBoutique implements IGestionBoutique {

    private EntityManager em;

    public GestionBoutique() {
        em = dao.SingletonConnection.getEntityManager();
    }

    // ======================
    // CREATE
    // ======================
    @Override
    public void ajouterBoutique(Boutique boutique) {
        em.getTransaction().begin();
        em.persist(boutique);
        em.getTransaction().commit();
    }

    // ======================
    // READ (par propriétaire)
    // ======================
    @Override
    public List<Boutique> listerBoutiquesParProprietaire(Proprietaire proprietaire) {

        return em.createQuery(
                "SELECT b FROM Boutique b WHERE b.proprietaire.id = :id",
                Boutique.class)
                .setParameter("id", proprietaire.getId())
                .getResultList();
    }

    public List<Boutique> listerBoutiques() {
        return em.createQuery("SELECT b FROM Boutique b", Boutique.class).getResultList();
    }

    // ======================
    // READ (par id)
    // ======================
    @Override
    public Boutique chercherBoutique(int idBoutique) {
        return em.find(Boutique.class, idBoutique);
    }

    // ======================
    // UPDATE
    // ======================
    @Override
    public void modifierBoutique(Boutique boutique) {
        em.getTransaction().begin();
        em.merge(boutique);
        em.getTransaction().commit();
    }

    // ======================
    // DELETE
    // ======================
    @Override
    public void supprimerBoutique(int idBoutique) {

        Boutique b = chercherBoutique(idBoutique);

        if (b != null) {
            em.getTransaction().begin();
            em.remove(b);
            em.getTransaction().commit();
        }
    }
}
