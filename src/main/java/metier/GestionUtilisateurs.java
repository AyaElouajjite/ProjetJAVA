package metier;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import dao.Boutique;
import dao.Proprietaire;
import dao.Reparateur;
import dao.Utilisateurs;
import exception.DuplicateException;
import util.PasswordUtils;

public class GestionUtilisateurs implements IGestionUtilisateurs {
    private EntityManager em;

    public GestionUtilisateurs() {
        em = dao.SingletonConnection.getEntityManager();
    }

    // Authentification avec vérification du mot de passe hashé
    @Override
    public Utilisateurs seConnecter(String email, String mdp) {
        try {
            // 1. Chercher l'utilisateur par email uniquement
            Utilisateurs u = em.createQuery(
                    "SELECT u FROM Utilisateurs u WHERE u.email = :email AND u.actif = true",
                    Utilisateurs.class)
                    .setParameter("email", email)
                    .getSingleResult();

            // 2. Vérifier le mot de passe hashé
            if (PasswordUtils.verifierMotDePasse(mdp, u.getMotDePasse())) {
                System.out.println("✅ Utilisateur trouvé : " + u.getNom() + " (" + u.getClass().getSimpleName() + ")");
                return u;
            } else {
                System.out.println("❌ Mot de passe incorrect.");
                return null;
            }

        } catch (NoResultException e) {
            System.out.println("❌ Aucun utilisateur trouvé pour cet email.");
            return null;
        } catch (Exception e) {
            System.out.println("❌ Erreur technique lors du login : ");
            e.printStackTrace();
            return null;
        }
    }

    // Inscription du Propriétaire (AVEC gestion doublon)
    @Override
    public void inscriptionProprietaire(Proprietaire p) throws DuplicateException {
        try {
            em.getTransaction().begin();
            p.setActif(true);
            em.persist(p);
            em.getTransaction().commit();
        } catch (PersistenceException ex) {
            em.getTransaction().rollback();
            ex.printStackTrace();
            throw new DuplicateException(
                    "❌ CIN ou Email déjà utilisé. (Ou erreur SQL : voir console)");
        }
    }

    // Ajout Réparateur (AVEC gestion doublon et hashage du mot de passe)
    @Override
    public void ajouterReparateur(Reparateur r, int idBoutique)
            throws DuplicateException {
        Boutique b = em.find(Boutique.class, (long) idBoutique);
        if (b == null) {
            throw new RuntimeException("❌ Boutique introuvable");
        }
        try {
            em.getTransaction().begin();
            r.setBoutique(b);
            r.setActif(true);
            // Hasher le mot de passe du réparateur
            r.setMotDePasse(PasswordUtils.hasherMotDePasse(r.getMotDePasse()));
            em.persist(r);
            em.getTransaction().commit();
        } catch (PersistenceException ex) {
            em.getTransaction().rollback();
            throw new DuplicateException(
                    "❌ Ce réparateur existe déjà (CIN ou Email)");
        }
    }

    /**
     * Rechercher un utilisateur par nom ou prénom
     */
    public List<Utilisateurs> chercherUtilisateurParNom(String nomOuPrenom) {
        String recherche = "%" + nomOuPrenom.trim() + "%";
        return em.createQuery(
                "SELECT u FROM Utilisateurs u WHERE (u.nom LIKE :recherche OR u.prenom LIKE :recherche) AND u.actif = true",
                Utilisateurs.class)
                .setParameter("recherche", recherche)
                .getResultList();
    }
}