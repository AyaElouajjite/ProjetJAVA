package metier;

import java.util.Date;

import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import dao.TypeAppareil;
import dao.AppareilReparation;

import dao.EtatReparation;
import dao.Reparation;

public class GestionReparation implements IGestionReparation {

    private EntityManager em;

    public GestionReparation() {
        this.em = dao.SingletonConnection.getEntityManager();
    }

    @Override
    public void ajouterReparation(Reparation r) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Génération code unique
            r.setCodeUnique(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            r.setDateCreation(new Date());

            // Initial State & Link Devices
            if (r.getEtat() == null)
                r.setEtat(EtatReparation.EN_ATTENTE);

            double total = 0;
            if (r.getAppareils() != null) {
                for (AppareilReparation ap : r.getAppareils()) {
                    ap.setReparation(r); // Link back
                    if (ap.getEtat() == null)
                        ap.setEtat(EtatReparation.EN_ATTENTE);
                    if (ap.getMontant() != null)
                        total += ap.getMontant();
                }
            }
            r.setMontant(total);

            em.persist(r);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            throw new RuntimeException("Erreur ajout réparation : " + e.getMessage(), e);
        }
    }

    @Override
    public List<Reparation> listerToutesReparations() {
        return em.createQuery("SELECT r FROM Reparation r", Reparation.class).getResultList();
    }

    @Override
    public List<Reparation> listerReparationsParBoutique(int boutiqueId) {
        return em.createQuery("SELECT r FROM Reparation r WHERE r.boutique.id = :id", Reparation.class)
                .setParameter("id", boutiqueId)
                .getResultList();
    }

    @Override
    public List<Reparation> listerReparationsParReparateur(int reparateurId) {
        return em.createQuery("SELECT r FROM Reparation r WHERE r.reparateur.id = :id", Reparation.class)
                .setParameter("id", reparateurId)
                .getResultList();
    }

    @Override
    public Reparation getReparation(int id) {
        return em.find(Reparation.class, id);
    }

    @Override
    public void modifierReparation(Reparation r) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(r);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void changerEtatAppareil(int appareilId, EtatReparation nouvelEtat) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            AppareilReparation ap = em.find(AppareilReparation.class, (long) appareilId); // Cast to Long if ID
                                                                                                  // is Long
            if (ap != null) {
                ap.setEtat(nouvelEtat);
                em.merge(ap);

                // Check Global Status
                Reparation r = ap.getReparation();
                checkAndFinalizeReparation(r, em);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            e.printStackTrace();
        }
    }

    private void checkAndFinalizeReparation(Reparation r, EntityManager em) {
        boolean allDelivered = true;
        boolean allFinished = true; // status TERMINÉ or LIVRÉ

        for (AppareilReparation ap : r.getAppareils()) {
            if (ap.getEtat() != EtatReparation.LIVRE) {
                allDelivered = false;
            }
            // Logic simpliste: Si un seul est encore EN_ATTENTE ou EN_COURS, le parent
            // n'est pas terminé
            if (ap.getEtat() == EtatReparation.EN_ATTENTE || ap.getEtat() == EtatReparation.EN_COURS) {
                allFinished = false;
            }
        }

        if (allDelivered) {
            // Si pas déjà livré, on livre le parent et on paie
            if (r.getEtat() != EtatReparation.LIVRE) {
                payer(r, em);
                r.setEtat(EtatReparation.LIVRE);
                em.merge(r);
            }
        } else if (allFinished) {
            r.setEtat(EtatReparation.TERMINE); // Prêt à être livré
            em.merge(r);
        } else {
            // Sinon, si le parent était EN_ATTENTE mais qu'on travaille dessus, passer
            // EN_COURS
            if (r.getEtat() == EtatReparation.EN_ATTENTE) {
                r.setEtat(EtatReparation.EN_COURS);
                em.merge(r);
            }
        }
    }

    private void payer(Reparation r, EntityManager em) {
        double montant = r.getMontant();
        dao.Reparateur rep = r.getReparateur();
        dao.Proprietaire prop = r.getBoutique().getProprietaire();
        dao.Boutique boutique = r.getBoutique();

        if (rep != null) {
            Double commissionBoutiquePourcentage = rep.getPourcentage();
            if (commissionBoutiquePourcentage == null)
                commissionBoutiquePourcentage = 0.0;

            double partBoutique = montant * (commissionBoutiquePourcentage / 100);
            double partReparateur = montant - partBoutique;

            rep.setSolde(rep.getSolde() + partReparateur);
            boutique.setSolde(boutique.getSolde() + partBoutique);

            em.merge(rep);
            em.merge(boutique);
        } else {
            prop.setSolde(prop.getSolde() + montant);
            em.merge(prop);
        }
    }

    @Override
    public void changerEtat(int id, EtatReparation nouvelEtat) {
        // Méthode Legacy / Force Override
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Reparation r = em.find(Reparation.class, id);
            if (r != null) {
                // Si on force LIVRE manuellement sur le parent, on considère tout livré ?
                if (r.getEtat() != EtatReparation.LIVRE && nouvelEtat == EtatReparation.LIVRE) {
                    payer(r, em);
                    // Force child status? Let's say yes for consistency
                    for (dao.AppareilReparation ap : r.getAppareils()) {
                        ap.setEtat(EtatReparation.LIVRE);
                        em.merge(ap);
                    }
                }
                r.setEtat(nouvelEtat);
                em.merge(r);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void supprimerReparation(int id) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Reparation r = em.find(Reparation.class, id);
            if (r != null) {
                em.remove(r);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            e.printStackTrace();
        }
    }
}
