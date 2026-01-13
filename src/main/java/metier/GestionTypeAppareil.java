package metier;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;

import dao.TypeAppareil;

public class GestionTypeAppareil implements IGestionTypeAppareil {

    private EntityManager em;

    public GestionTypeAppareil() {
        this.em = dao.SingletonConnection.getEntityManager();
    }

    @Override
    public void ajouterTypeAppareil(TypeAppareil t) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(t);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            throw e;
        }
    }

    @Override
    public List<TypeAppareil> listerTypesAppareil() {
        return em.createQuery("SELECT t FROM TypeAppareil t", TypeAppareil.class).getResultList();
    }

    @Override
    public void supprimerTypeAppareil(int id) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TypeAppareil t = em.find(TypeAppareil.class, id);
            if (t != null) {
                em.remove(t);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            throw e;
        }
    }

    @Override
    public TypeAppareil chercherTypeAppareil(String nom) {
        try {
            return em.createQuery("SELECT t FROM TypeAppareil t WHERE t.nom = :nom", TypeAppareil.class)
                    .setParameter("nom", nom)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
