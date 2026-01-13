package metier;

import java.util.List;
import javax.persistence.EntityManager;
import dao.Boutique;
import dao.Reparateur;
import util.PasswordUtils;

public class GestionReparateur implements IGestionReparateur {
    private EntityManager em;

    public GestionReparateur() {
        em = dao.SingletonConnection.getEntityManager();
    }

    // ======================
    // CREATE
    // ======================
    @Override
    public void ajouterReparateur(Reparateur reparateur, int idBoutique) {
        Boutique b = em.find(Boutique.class, idBoutique);
        if (b == null) {
            throw new RuntimeException("Boutique introuvable");
        }
        em.getTransaction().begin();
        reparateur.setBoutique(b);
        reparateur.setActif(true);
        // Hasher le mot de passe avant de sauvegarder
        reparateur.setMotDePasse(PasswordUtils.hasherMotDePasse(reparateur.getMotDePasse()));
        em.persist(reparateur);
        em.getTransaction().commit();
    }

    // ======================
    // READ (par boutique)
    // ======================
    @Override
    public List<Reparateur> listerReparateursParBoutique(int idBoutique) {
        em.clear();
        return em.createQuery(
                "SELECT r FROM Reparateur r WHERE r.boutique.id = :id",
                Reparateur.class)
                .setParameter("id", idBoutique)
                .getResultList();
    }

    // ======================
    // READ (par id)
    // ======================
    @Override
    public Reparateur chercherReparateur(int idReparateur) {
        return em.find(Reparateur.class, idReparateur);
    }

    // ======================
    // UPDATE
    // ======================
    @Override
    public void modifierReparateur(Reparateur reparateur) {
        em.getTransaction().begin();
        em.merge(reparateur);
        em.getTransaction().commit();
    }

    // ======================
    // DELETE
    // ======================
    @Override
    public void supprimerReparateur(int idReparateur) {
        Reparateur r = em.find(Reparateur.class, idReparateur);
        if (r != null) {
            em.getTransaction().begin();
            em.remove(r);
            em.getTransaction().commit();
        }
    }
}