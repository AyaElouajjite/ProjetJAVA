package metier;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import dao.TransactionFinanciere;
import dao.Utilisateurs;
import dao.EtatTransaction;
import dao.TypeTransaction;

public class GestionTransactionFinanciere implements IGestionTransactionFinanciere {

    private EntityManager em;

    public GestionTransactionFinanciere() {
        this.em = dao.SingletonConnection.getEntityManager();
    }

    @Override
    public void creerTransaction(TransactionFinanciere transaction) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // Valider les données
            if (transaction.getMontant() <= 0) {
                throw new IllegalArgumentException("Le montant doit être positif");
            }

            // Vérifier qu'au moins une partie (prêteur ou emprunteur) est un utilisateur du système
            Utilisateurs preteur = null;
            Utilisateurs emprunteur = null;
            
            if (transaction.getPreteur() != null) {
                preteur = em.find(Utilisateurs.class, transaction.getPreteur().getId());
                if (preteur == null) {
                    throw new IllegalArgumentException("Prêteur introuvable");
                }
            }
            
            if (transaction.getEmprunteur() != null) {
                emprunteur = em.find(Utilisateurs.class, transaction.getEmprunteur().getId());
                if (emprunteur == null) {
                    throw new IllegalArgumentException("Emprunteur introuvable");
                }
            }

            // Si les deux parties sont des utilisateurs du système, vérifier qu'ils sont différents
            if (preteur != null && emprunteur != null && preteur.getId() == emprunteur.getId()) {
                throw new IllegalArgumentException("Un utilisateur ne peut pas faire une transaction avec lui-même");
            }

            // Si le prêteur est un utilisateur du système, vérifier qu'il a suffisamment de fonds
            if (preteur != null) {
                double soldeEffectifPreteur = preteur.getSolde() + preteur.getSoldeTemporaire();
                if (soldeEffectifPreteur < transaction.getMontant()) {
                    throw new IllegalArgumentException(
                            "Solde insuffisant. Solde disponible : " + String.format("%.2f", soldeEffectifPreteur));
                }
            }

            // Initialiser l'état si non défini
            if (transaction.getEtat() == null) {
                transaction.setEtat(EtatTransaction.EN_ATTENTE);
            }

            // Appliquer les ajustements temporaires uniquement si les parties sont des utilisateurs du système
            if (transaction.getTypeTransaction() == TypeTransaction.PRET && preteur != null) {
                // Le prêteur perd temporairement le montant
                preteur.setSoldeTemporaire(preteur.getSoldeTemporaire() - transaction.getMontant());
                em.merge(preteur);
                
                // Si l'emprunteur est aussi un utilisateur du système, il gagne temporairement
                if (emprunteur != null) {
                    emprunteur.setSoldeTemporaire(emprunteur.getSoldeTemporaire() + transaction.getMontant());
                    em.merge(emprunteur);
                }
            } else if (transaction.getTypeTransaction() == TypeTransaction.EMPRUNT && emprunteur != null) {
                // L'emprunteur gagne temporairement le montant
                emprunteur.setSoldeTemporaire(emprunteur.getSoldeTemporaire() + transaction.getMontant());
                em.merge(emprunteur);
                
                // Si le prêteur est aussi un utilisateur du système, il perd temporairement
                if (preteur != null) {
                    preteur.setSoldeTemporaire(preteur.getSoldeTemporaire() - transaction.getMontant());
                    em.merge(preteur);
                }
            }

            // Sauvegarder la transaction
            transaction.setPreteur(preteur);
            transaction.setEmprunteur(emprunteur);
            em.persist(transaction);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erreur lors de la création de la transaction : " + e.getMessage(), e);
        }
    }

    @Override
    public void confirmerTransaction(int idTransaction, int idUtilisateurConfirmeur) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            TransactionFinanciere transaction = em.find(TransactionFinanciere.class, idTransaction);
            if (transaction == null) {
                throw new IllegalArgumentException("Transaction introuvable");
            }

            if (transaction.getEtat() != EtatTransaction.EN_ATTENTE) {
                throw new IllegalArgumentException("Seules les transactions en attente peuvent être confirmées");
            }

            // Vérifier que l'utilisateur peut confirmer cette transaction
            Utilisateurs utilisateur = em.find(Utilisateurs.class, idUtilisateurConfirmeur);
            if (utilisateur == null) {
                throw new IllegalArgumentException("Utilisateur introuvable");
            }

            // Seul le prêteur ou l'emprunteur (utilisateur du système) peut confirmer
            boolean peutConfirmer = false;
            if (transaction.getPreteur() != null && transaction.getPreteur().getId() == idUtilisateurConfirmeur) {
                peutConfirmer = true;
            } else if (transaction.getEmprunteur() != null && transaction.getEmprunteur().getId() == idUtilisateurConfirmeur) {
                peutConfirmer = true;
            }
            
            if (!peutConfirmer) {
                throw new IllegalArgumentException("Vous n'êtes pas autorisé à confirmer cette transaction");
            }

            // Recharger les utilisateurs (peuvent être null si personne externe)
            Utilisateurs preteur = transaction.getPreteur() != null ? 
                em.find(Utilisateurs.class, transaction.getPreteur().getId()) : null;
            Utilisateurs emprunteur = transaction.getEmprunteur() != null ? 
                em.find(Utilisateurs.class, transaction.getEmprunteur().getId()) : null;

            // Convertir les ajustements temporaires en ajustements définitifs selon le type
            // Pour EMPRUNT PAS_RENDU : l'emprunteur reçoit l'argent au solde définitif
            // Pour PRET NON_REMBOURSE : le prêteur perd l'argent du solde définitif
            if (transaction.getTypeTransaction() == TypeTransaction.EMPRUNT && emprunteur != null) {
                // L'emprunteur gagne définitivement le montant (il a reçu l'argent)
                emprunteur.setSolde(emprunteur.getSolde() + transaction.getMontant());
                emprunteur.setSoldeTemporaire(emprunteur.getSoldeTemporaire() - transaction.getMontant()); // Annuler le temporaire
                em.merge(emprunteur);
                
                // Si le prêteur est aussi un utilisateur du système
                if (preteur != null) {
                    preteur.setSolde(preteur.getSolde() - transaction.getMontant());
                    preteur.setSoldeTemporaire(preteur.getSoldeTemporaire() + transaction.getMontant()); // Annuler le temporaire
                    em.merge(preteur);
                }
            } else if (transaction.getTypeTransaction() == TypeTransaction.PRET && preteur != null) {
                // Le prêteur perd définitivement le montant (il a prêté l'argent)
                preteur.setSolde(preteur.getSolde() - transaction.getMontant());
                preteur.setSoldeTemporaire(preteur.getSoldeTemporaire() + transaction.getMontant()); // Annuler le temporaire
                em.merge(preteur);
                
                // Si l'emprunteur est aussi un utilisateur du système
                if (emprunteur != null) {
                    emprunteur.setSolde(emprunteur.getSolde() + transaction.getMontant());
                    emprunteur.setSoldeTemporaire(emprunteur.getSoldeTemporaire() - transaction.getMontant()); // Annuler le temporaire
                    em.merge(emprunteur);
                }
            }

            // Mettre à jour la transaction selon le type
            if (transaction.getTypeTransaction() == TypeTransaction.EMPRUNT) {
                transaction.setEtat(EtatTransaction.PAS_RENDU); // Emprunt confirmé mais pas encore rendu
            } else {
                transaction.setEtat(EtatTransaction.NON_REMBOURSE); // Prêt confirmé mais pas encore remboursé
            }
            transaction.setDateConfirmation(LocalDateTime.now());

            // Sauvegarder
            em.merge(transaction);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erreur lors de la confirmation de la transaction : " + e.getMessage(), e);
        }
    }

    @Override
    public void annulerTransaction(int idTransaction, int idUtilisateurAnnuleur) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            TransactionFinanciere transaction = em.find(TransactionFinanciere.class, idTransaction);
            if (transaction == null) {
                throw new IllegalArgumentException("Transaction introuvable");
            }

            if (transaction.getEtat() != EtatTransaction.EN_ATTENTE) {
                throw new IllegalArgumentException("Seules les transactions en attente peuvent être annulées");
            }

            // Vérifier que l'utilisateur peut annuler cette transaction
            Utilisateurs utilisateur = em.find(Utilisateurs.class, idUtilisateurAnnuleur);
            if (utilisateur == null) {
                throw new IllegalArgumentException("Utilisateur introuvable");
            }

            // Seul le prêteur ou l'emprunteur (utilisateur du système) peut annuler
            boolean peutAnnuler = false;
            if (transaction.getPreteur() != null && transaction.getPreteur().getId() == idUtilisateurAnnuleur) {
                peutAnnuler = true;
            } else if (transaction.getEmprunteur() != null && transaction.getEmprunteur().getId() == idUtilisateurAnnuleur) {
                peutAnnuler = true;
            }
            
            if (!peutAnnuler) {
                throw new IllegalArgumentException("Vous n'êtes pas autorisé à annuler cette transaction");
            }

            // Recharger les utilisateurs (peuvent être null si personne externe)
            Utilisateurs preteur = transaction.getPreteur() != null ? 
                em.find(Utilisateurs.class, transaction.getPreteur().getId()) : null;
            Utilisateurs emprunteur = transaction.getEmprunteur() != null ? 
                em.find(Utilisateurs.class, transaction.getEmprunteur().getId()) : null;

            // Annuler les ajustements temporaires (inverser) - uniquement pour utilisateurs du système
            if (transaction.getTypeTransaction() == TypeTransaction.PRET && preteur != null) {
                // Le prêteur récupère le montant temporairement perdu
                preteur.setSoldeTemporaire(preteur.getSoldeTemporaire() + transaction.getMontant());
                em.merge(preteur);
                
                // Si l'emprunteur est aussi un utilisateur du système
                if (emprunteur != null) {
                    emprunteur.setSoldeTemporaire(emprunteur.getSoldeTemporaire() - transaction.getMontant());
                    em.merge(emprunteur);
                }
            } else if (transaction.getTypeTransaction() == TypeTransaction.EMPRUNT && emprunteur != null) {
                // L'emprunteur perd le montant temporairement gagné
                emprunteur.setSoldeTemporaire(emprunteur.getSoldeTemporaire() - transaction.getMontant());
                em.merge(emprunteur);
                
                // Si le prêteur est aussi un utilisateur du système
                if (preteur != null) {
                    preteur.setSoldeTemporaire(preteur.getSoldeTemporaire() + transaction.getMontant());
                    em.merge(preteur);
                }
            }

            // Mettre à jour la transaction
            transaction.setEtat(EtatTransaction.ANNULE);

            // Sauvegarder
            em.merge(transaction);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erreur lors de l'annulation de la transaction : " + e.getMessage(), e);
        }
    }

    @Override
    public List<TransactionFinanciere> listerTransactionsParUtilisateur(int idUtilisateur) {
        return em.createQuery(
                "SELECT t FROM TransactionFinanciere t WHERE (t.preteur.id = :id OR t.emprunteur.id = :id) ORDER BY t.dateCreation DESC",
                TransactionFinanciere.class)
                .setParameter("id", idUtilisateur)
                .getResultList();
    }

    @Override
    public List<TransactionFinanciere> listerTransactionsEnAttente(int idUtilisateur) {
        return em.createQuery(
                "SELECT t FROM TransactionFinanciere t WHERE (t.preteur.id = :id OR t.emprunteur.id = :id) AND t.etat = :etat ORDER BY t.dateCreation DESC",
                TransactionFinanciere.class)
                .setParameter("id", idUtilisateur)
                .setParameter("etat", EtatTransaction.EN_ATTENTE)
                .getResultList();
    }

    @Override
    public TransactionFinanciere getTransaction(int idTransaction) {
        return em.find(TransactionFinanciere.class, idTransaction);
    }

    @Override
    public double calculerSoldeEffectif(int idUtilisateur) {
        Utilisateurs utilisateur = em.find(Utilisateurs.class, idUtilisateur);
        if (utilisateur == null) {
            return 0.0;
        }
        return utilisateur.getSolde() + utilisateur.getSoldeTemporaire();
    }

    @Override
    public void modifierEtatTransaction(int idTransaction, EtatTransaction nouvelEtat, int idUtilisateur) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            TransactionFinanciere transaction = em.find(TransactionFinanciere.class, idTransaction);
            if (transaction == null) {
                throw new IllegalArgumentException("Transaction introuvable");
            }

            // Vérifier que l'utilisateur peut modifier cette transaction
            boolean peutModifier = false;
            if (transaction.getPreteur() != null && transaction.getPreteur().getId() == idUtilisateur) {
                peutModifier = true;
            } else if (transaction.getEmprunteur() != null && transaction.getEmprunteur().getId() == idUtilisateur) {
                peutModifier = true;
            }

            if (!peutModifier) {
                throw new IllegalArgumentException("Vous n'êtes pas autorisé à modifier cette transaction");
            }

            EtatTransaction ancienEtat = transaction.getEtat();
            
            // Si l'état ne change pas, ne rien faire
            if (ancienEtat == nouvelEtat) {
                tx.commit();
                return;
            }

            // Recharger les utilisateurs (peuvent être null si personne externe)
            Utilisateurs preteur = transaction.getPreteur() != null ? 
                em.find(Utilisateurs.class, transaction.getPreteur().getId()) : null;
            Utilisateurs emprunteur = transaction.getEmprunteur() != null ? 
                em.find(Utilisateurs.class, transaction.getEmprunteur().getId()) : null;

            double montant = transaction.getMontant();

            // Annuler les effets de l'ancien état
            if (ancienEtat == EtatTransaction.EN_ATTENTE) {
                // Annuler les ajustements temporaires
                if (transaction.getTypeTransaction() == TypeTransaction.PRET && preteur != null) {
                    preteur.setSoldeTemporaire(preteur.getSoldeTemporaire() + montant);
                    if (emprunteur != null) {
                        emprunteur.setSoldeTemporaire(emprunteur.getSoldeTemporaire() - montant);
                    }
                } else if (transaction.getTypeTransaction() == TypeTransaction.EMPRUNT && emprunteur != null) {
                    emprunteur.setSoldeTemporaire(emprunteur.getSoldeTemporaire() - montant);
                    if (preteur != null) {
                        preteur.setSoldeTemporaire(preteur.getSoldeTemporaire() + montant);
                    }
                }
            } else if (ancienEtat == EtatTransaction.PAS_RENDU) {
                // Annuler les effets de PAS_RENDU (emprunt)
                // L'emprunteur avait reçu l'argent au solde définitif, on doit l'annuler
                if (transaction.getTypeTransaction() == TypeTransaction.EMPRUNT && emprunteur != null) {
                    emprunteur.setSolde(emprunteur.getSolde() - montant);
                    if (preteur != null) {
                        preteur.setSolde(preteur.getSolde() + montant);
                    }
                }
            } else if (ancienEtat == EtatTransaction.NON_REMBOURSE) {
                // Annuler les effets de NON_REMBOURSE (prêt)
                // Le prêteur avait perdu l'argent du solde définitif, on doit l'annuler
                if (transaction.getTypeTransaction() == TypeTransaction.PRET && preteur != null) {
                    preteur.setSolde(preteur.getSolde() + montant);
                    if (emprunteur != null) {
                        emprunteur.setSolde(emprunteur.getSolde() - montant);
                    }
                }
            } else if (ancienEtat == EtatTransaction.RENDU) {
                // Annuler les effets de RENDU (emprunt rendu)
                // L'emprunteur avait remboursé, on doit annuler ce remboursement
                if (transaction.getTypeTransaction() == TypeTransaction.EMPRUNT && emprunteur != null) {
                    emprunteur.setSolde(emprunteur.getSolde() + montant);
                    if (preteur != null) {
                        preteur.setSolde(preteur.getSolde() - montant);
                    }
                }
            } else if (ancienEtat == EtatTransaction.REMBOURSE) {
                // Annuler les effets de REMBOURSE (prêt remboursé)
                // Le prêteur avait été remboursé, on doit annuler ce remboursement
                if (transaction.getTypeTransaction() == TypeTransaction.PRET && preteur != null) {
                    preteur.setSolde(preteur.getSolde() - montant);
                    if (emprunteur != null) {
                        emprunteur.setSolde(emprunteur.getSolde() + montant);
                    }
                }
            }
            // Si ANNULE, rien à annuler car déjà annulé

            // Appliquer les effets du nouvel état
            if (nouvelEtat == EtatTransaction.EN_ATTENTE) {
                // Appliquer les ajustements temporaires
                if (transaction.getTypeTransaction() == TypeTransaction.PRET && preteur != null) {
                    preteur.setSoldeTemporaire(preteur.getSoldeTemporaire() - montant);
                    if (emprunteur != null) {
                        emprunteur.setSoldeTemporaire(emprunteur.getSoldeTemporaire() + montant);
                    }
                } else if (transaction.getTypeTransaction() == TypeTransaction.EMPRUNT && emprunteur != null) {
                    emprunteur.setSoldeTemporaire(emprunteur.getSoldeTemporaire() + montant);
                    if (preteur != null) {
                        preteur.setSoldeTemporaire(preteur.getSoldeTemporaire() - montant);
                    }
                }
            } else if (nouvelEtat == EtatTransaction.PAS_RENDU) {
                // EMPRUNT PAS_RENDU : L'emprunteur a reçu l'argent → ajouter au solde définitif
                if (transaction.getTypeTransaction() == TypeTransaction.EMPRUNT && emprunteur != null) {
                    emprunteur.setSolde(emprunteur.getSolde() + montant);
                    // Annuler le temporaire s'il existe
                    emprunteur.setSoldeTemporaire(emprunteur.getSoldeTemporaire() - montant);
                    if (preteur != null) {
                        preteur.setSolde(preteur.getSolde() - montant);
                        preteur.setSoldeTemporaire(preteur.getSoldeTemporaire() + montant);
                    }
                }
            } else if (nouvelEtat == EtatTransaction.RENDU) {
                // EMPRUNT RENDU : L'emprunteur rembourse → débit du solde définitif
                if (transaction.getTypeTransaction() == TypeTransaction.EMPRUNT && emprunteur != null) {
                    emprunteur.setSolde(emprunteur.getSolde() - montant);
                    if (preteur != null) {
                        preteur.setSolde(preteur.getSolde() + montant);
                    }
                }
                transaction.setDateConfirmation(LocalDateTime.now());
            } else if (nouvelEtat == EtatTransaction.NON_REMBOURSE) {
                // PRET NON_REMBOURSE : Le prêteur a prêté l'argent → débit du solde définitif
                if (transaction.getTypeTransaction() == TypeTransaction.PRET && preteur != null) {
                    preteur.setSolde(preteur.getSolde() - montant);
                    // Annuler le temporaire s'il existe
                    preteur.setSoldeTemporaire(preteur.getSoldeTemporaire() + montant);
                    if (emprunteur != null) {
                        emprunteur.setSolde(emprunteur.getSolde() + montant);
                        emprunteur.setSoldeTemporaire(emprunteur.getSoldeTemporaire() - montant);
                    }
                }
            } else if (nouvelEtat == EtatTransaction.REMBOURSE) {
                // PRET REMBOURSE : Le prêteur est remboursé → crédit du solde définitif
                if (transaction.getTypeTransaction() == TypeTransaction.PRET && preteur != null) {
                    preteur.setSolde(preteur.getSolde() + montant);
                    if (emprunteur != null) {
                        emprunteur.setSolde(emprunteur.getSolde() - montant);
                    }
                }
                transaction.setDateConfirmation(LocalDateTime.now());
            }
            // Si ANNULE, rien à appliquer (déjà annulé)

            // Mettre à jour l'état de la transaction
            transaction.setEtat(nouvelEtat);

            // Sauvegarder les utilisateurs
            if (preteur != null) {
                em.merge(preteur);
            }
            if (emprunteur != null) {
                em.merge(emprunteur);
            }

            // Sauvegarder la transaction
            em.merge(transaction);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erreur lors de la modification de l'état : " + e.getMessage(), e);
        }
    }
}
