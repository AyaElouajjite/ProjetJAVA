package metier;

import java.util.List;
import dao.TransactionFinanciere;
import dao.Utilisateurs;
import dao.EtatTransaction;

public interface IGestionTransactionFinanciere {

    /**
     * Créer une nouvelle transaction (prêt ou emprunt)
     * Les ajustements temporaires sont appliqués automatiquement
     */
    void creerTransaction(TransactionFinanciere transaction);

    /**
     * Confirmer une transaction (passe de EN_ATTENTE à RENDU/REMBOURSE selon le type)
     * Les ajustements temporaires deviennent définitifs
     */
    void confirmerTransaction(int idTransaction, int idUtilisateurConfirmeur);

    /**
     * Annuler une transaction (passe de EN_ATTENTE à ANNULE)
     * Les ajustements temporaires sont annulés
     */
    void annulerTransaction(int idTransaction, int idUtilisateurAnnuleur);

    /**
     * Lister toutes les transactions d'un utilisateur
     */
    List<TransactionFinanciere> listerTransactionsParUtilisateur(int idUtilisateur);

    /**
     * Lister les transactions en attente d'un utilisateur
     */
    List<TransactionFinanciere> listerTransactionsEnAttente(int idUtilisateur);

    /**
     * Obtenir une transaction par son ID
     */
    TransactionFinanciere getTransaction(int idTransaction);

    /**
     * Calculer le solde effectif d'un utilisateur
     * Solde effectif = solde + soldeTemporaire
     */
    double calculerSoldeEffectif(int idUtilisateur);

    /**
     * Modifier l'état d'une transaction et mettre à jour la caisse en conséquence
     */
    void modifierEtatTransaction(int idTransaction, EtatTransaction nouvelEtat, int idUtilisateur);
}
