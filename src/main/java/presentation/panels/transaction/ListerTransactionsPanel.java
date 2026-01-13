package presentation.panels.transaction;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import dao.TransactionFinanciere;
import dao.Utilisateurs;
import dao.EtatTransaction;
import dao.TypeTransaction;
import metier.GestionTransactionFinanciere;
import metier.IGestionTransactionFinanciere;

public class ListerTransactionsPanel extends JPanel {

    private Utilisateurs utilisateurConnecte;
    private JTable tableTransactions;
    private DefaultTableModel model;
    private JTextField txtRecherche;
    private JLabel lblSolde, lblSoldeTemporaire, lblSoldeEffectif;
    private List<TransactionFinanciere> listeTransactions;
    private IGestionTransactionFinanciere gestionTransaction;

    public ListerTransactionsPanel(Utilisateurs utilisateur) {
        this.utilisateurConnecte = utilisateur;
        this.gestionTransaction = new GestionTransactionFinanciere();
        this.listeTransactions = new java.util.ArrayList<>();

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. STATS EN HAUT
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));

        lblSolde = createStatCard("💰 Solde Définitif", Color.decode("#4CAF50"));
        lblSoldeTemporaire = createStatCard("⏳ Solde Temporaire", Color.decode("#FF9800"));
        lblSoldeEffectif = createStatCard("💵 Solde Effectif", Color.decode("#2196F3"));

        statsPanel.add(lblSolde);
        statsPanel.add(lblSoldeTemporaire);
        statsPanel.add(lblSoldeEffectif);

        add(statsPanel, BorderLayout.NORTH);

        // 2. Panel des transactions
        JPanel listPanel = new JPanel(new BorderLayout(5, 5));

        // Header avec titre et recherche
        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel titre = new JLabel("Mes Transactions Financières :");
        titre.setFont(new Font("Arial", Font.BOLD, 12));

        // Barre de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.add(new JLabel("Rechercher :"));
        txtRecherche = new JTextField(20);
        searchPanel.add(txtRecherche);

        JButton btnRechercher = new JButton("Rechercher");
        JButton btnAfficherTout = new JButton("Afficher tout");

        searchPanel.add(btnRechercher);
        searchPanel.add(btnAfficherTout);

        // Actions recherche
        btnRechercher.addActionListener(e -> rechercher());
        btnAfficherTout.addActionListener(e -> {
            txtRecherche.setText("");
            afficherToutesTransactions();
        });

        // Recherche en temps réel
        txtRecherche.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                rechercher();
            }
        });

        headerPanel.add(titre, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        // Table
        String[] colonnes = {"ID", "Type", "Avec", "Montant", "Date", "État", "Description"};
        model = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableTransactions = new JTable(model);
        tableTransactions.setRowHeight(25);
        tableTransactions.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        listPanel.add(headerPanel, BorderLayout.NORTH);
        listPanel.add(new JScrollPane(tableTransactions), BorderLayout.CENTER);

        // Boutons d'action
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnModifierEtat = new JButton("Modifier l'état");
        JButton btnVoirDetails = new JButton("Voir détails");

        btnModifierEtat.setBackground(Color.decode("#2196F3"));
        btnModifierEtat.setForeground(Color.WHITE);
        btnModifierEtat.setFocusPainted(false);

        btnModifierEtat.addActionListener(e -> modifierEtatTransaction());
        btnVoirDetails.addActionListener(e -> voirDetails());

        btnPanel.add(btnModifierEtat);
        btnPanel.add(btnVoirDetails);

        listPanel.add(btnPanel, BorderLayout.SOUTH);

        add(listPanel, BorderLayout.CENTER);

        refresh();
    }

    private JLabel createStatCard(String title, Color bgColor) {
        JLabel lbl = new JLabel("...");
        lbl.setFont(new Font("Arial", Font.BOLD, 20));
        lbl.setHorizontalAlignment(JLabel.CENTER);
        lbl.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(bgColor, 2),
                title,
                0, 0, new Font("Arial", Font.BOLD, 12), bgColor));
        return lbl;
    }

    private void rechercher() {
        String recherche = txtRecherche.getText().trim().toLowerCase();
        model.setRowCount(0);

        for (TransactionFinanciere t : listeTransactions) {
            String description = (t.getDescription() != null) ? t.getDescription().toLowerCase() : "";
            String autrePartie = getAutrePartieNom(t).toLowerCase();

            if (recherche.isEmpty() || description.contains(recherche) || autrePartie.contains(recherche)) {
                ajouterLigneTable(t);
            }
        }
    }

    private void afficherToutesTransactions() {
        model.setRowCount(0);
        for (TransactionFinanciere t : listeTransactions) {
            ajouterLigneTable(t);
        }
    }

    private void ajouterLigneTable(TransactionFinanciere t) {
        String type = t.getTypeTransaction() == TypeTransaction.PRET ? "Prêt" : "Emprunt";
        String autrePartie = getAutrePartieNom(t);
        String etat = getEtatString(t.getEtat(), t.getTypeTransaction());
        String description = t.getDescription() != null && !t.getDescription().isEmpty() 
                ? (t.getDescription().length() > 30 ? t.getDescription().substring(0, 30) + "..." : t.getDescription())
                : "-";

        model.addRow(new Object[]{
                t.getId(),
                type,
                autrePartie,
                String.format("%.2f MAD", t.getMontant()),
                t.getDateCreation() != null ? t.getDateCreation().toString() : "-",
                etat,
                description
        });
    }

    private String getAutrePartieNom(TransactionFinanciere t) {
        // Obtenir l'autre partie par rapport à l'utilisateur connecté
        if (t.getPreteur() != null && t.getPreteur().getId() == utilisateurConnecte.getId()) {
            // L'utilisateur connecté est le prêteur, l'autre partie est l'emprunteur
            if (t.getEmprunteur() != null) {
                return t.getEmprunteur().getNom() + " " + t.getEmprunteur().getPrenom();
            } else {
                return t.getNomEmprunteurExterne() != null ? t.getNomEmprunteurExterne() : "Personne externe";
            }
        } else if (t.getEmprunteur() != null && t.getEmprunteur().getId() == utilisateurConnecte.getId()) {
            // L'utilisateur connecté est l'emprunteur, l'autre partie est le prêteur
            if (t.getPreteur() != null) {
                return t.getPreteur().getNom() + " " + t.getPreteur().getPrenom();
            } else {
                return t.getNomPreteurExterne() != null ? t.getNomPreteurExterne() : "Personne externe";
            }
        }
        return "?";
    }

    private String getEtatString(EtatTransaction etat, TypeTransaction typeTransaction) {
        switch (etat) {
            case EN_ATTENTE:
                return "⏳ En attente";
            case RENDU:
                return "✅ Rendu";
            case PAS_RENDU:
                return "⏸️ Pas rendu";
            case REMBOURSE:
                return "✅ Remboursé";
            case NON_REMBOURSE:
                return "⏸️ Non remboursé";
            case ANNULE:
                return "❌ Annulé";
            default:
                return etat.toString();
        }
    }
    
    private String getEtatString(EtatTransaction etat) {
        // Pour compatibilité avec les anciens appels, on utilise le type par défaut
        return getEtatString(etat, null);
    }

    private void modifierEtatTransaction() {
        int row = tableTransactions.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une transaction", "Erreur",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idTransaction = (int) model.getValueAt(row, 0);
        TransactionFinanciere transaction = listeTransactions.stream()
                .filter(t -> t.getId() == idTransaction)
                .findFirst()
                .orElse(null);

        if (transaction == null) {
            JOptionPane.showMessageDialog(this, "Transaction introuvable", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Créer un dialogue pour sélectionner le nouvel état selon le type de transaction
        EtatTransaction[] etatsDisponibles;
        if (transaction.getTypeTransaction() == TypeTransaction.EMPRUNT) {
            // Pour un emprunt: EN_ATTENTE, RENDU, PAS_RENDU, ANNULE
            etatsDisponibles = new EtatTransaction[]{
                EtatTransaction.EN_ATTENTE,
                EtatTransaction.RENDU,
                EtatTransaction.PAS_RENDU,
                EtatTransaction.ANNULE
            };
        } else {
            // Pour un prêt: EN_ATTENTE, REMBOURSE, NON_REMBOURSE, ANNULE
            etatsDisponibles = new EtatTransaction[]{
                EtatTransaction.EN_ATTENTE,
                EtatTransaction.REMBOURSE,
                EtatTransaction.NON_REMBOURSE,
                EtatTransaction.ANNULE
            };
        }
        
        String[] options = new String[etatsDisponibles.length];
        for (int i = 0; i < etatsDisponibles.length; i++) {
            options[i] = getEtatString(etatsDisponibles[i], transaction.getTypeTransaction());
        }

        int choix = JOptionPane.showOptionDialog(this,
                "Sélectionnez le nouvel état pour cette transaction :\n\n" +
                        "Type : " + (transaction.getTypeTransaction() == TypeTransaction.PRET ? "Prêt" : "Emprunt") + "\n" +
                        "État actuel : " + getEtatString(transaction.getEtat(), transaction.getTypeTransaction()) + "\n" +
                        "Montant : " + String.format("%.2f MAD", transaction.getMontant()),
                "Modifier l'état",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choix >= 0 && choix < etatsDisponibles.length) {
            EtatTransaction nouvelEtat = etatsDisponibles[choix];
            
            if (nouvelEtat == transaction.getEtat()) {
                JOptionPane.showMessageDialog(this, "L'état est déjà " + getEtatString(nouvelEtat, transaction.getTypeTransaction()),
                        "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String message = "Êtes-vous sûr de vouloir changer l'état de " + 
                    getEtatString(transaction.getEtat(), transaction.getTypeTransaction()) + " vers " + 
                    getEtatString(nouvelEtat, transaction.getTypeTransaction()) + " ?\n\n";
            
            if (nouvelEtat == EtatTransaction.RENDU || nouvelEtat == EtatTransaction.REMBOURSE) {
                message += "La transaction sera marquée comme terminée et la caisse sera mise à jour.";
            } else if (nouvelEtat == EtatTransaction.PAS_RENDU || nouvelEtat == EtatTransaction.NON_REMBOURSE) {
                message += "La transaction sera marquée comme en cours et la caisse sera mise à jour.";
            } else if (nouvelEtat == EtatTransaction.ANNULE) {
                message += "Les ajustements seront annulés et la caisse sera mise à jour.";
            } else if (nouvelEtat == EtatTransaction.EN_ATTENTE) {
                message += "Les ajustements redeviendront temporaires et la caisse sera mise à jour.";
            }

            int confirmation = JOptionPane.showConfirmDialog(this, message,
                    "Confirmer", JOptionPane.YES_NO_OPTION);

            if (confirmation == JOptionPane.YES_OPTION) {
                try {
                    gestionTransaction.modifierEtatTransaction(idTransaction, nouvelEtat, utilisateurConnecte.getId());
                    JOptionPane.showMessageDialog(this, 
                            "État modifié avec succès.\nLa caisse a été mise à jour.", 
                            "Succès", JOptionPane.INFORMATION_MESSAGE);
                    refresh();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage(), "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        }
    }

    private void voirDetails() {
        int row = tableTransactions.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une transaction", "Erreur",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idTransaction = (int) model.getValueAt(row, 0);
        TransactionFinanciere transaction = listeTransactions.stream()
                .filter(t -> t.getId() == idTransaction)
                .findFirst()
                .orElse(null);

        if (transaction == null) {
            JOptionPane.showMessageDialog(this, "Transaction introuvable", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("=== Détails de la Transaction ===\n\n");
        details.append("ID : ").append(transaction.getId()).append("\n");
        details.append("Type : ").append(transaction.getTypeTransaction() == TypeTransaction.PRET ? "Prêt" : "Emprunt").append("\n");
        
        String nomPreteur = transaction.getPreteur() != null ? 
            (transaction.getPreteur().getNom() + " " + transaction.getPreteur().getPrenom()) : 
            (transaction.getNomPreteurExterne() != null ? transaction.getNomPreteurExterne() + " (Externe)" : "Non spécifié");
        String nomEmprunteur = transaction.getEmprunteur() != null ? 
            (transaction.getEmprunteur().getNom() + " " + transaction.getEmprunteur().getPrenom()) : 
            (transaction.getNomEmprunteurExterne() != null ? transaction.getNomEmprunteurExterne() + " (Externe)" : "Non spécifié");
        
        details.append("Prêteur : ").append(nomPreteur).append("\n");
        details.append("Emprunteur : ").append(nomEmprunteur).append("\n");
        details.append("Montant : ").append(String.format("%.2f MAD", transaction.getMontant())).append("\n");
        details.append("État : ").append(getEtatString(transaction.getEtat(), transaction.getTypeTransaction())).append("\n");
        details.append("Date création : ").append(transaction.getDateCreation() != null ? transaction.getDateCreation().toString() : "-").append("\n");
        if (transaction.getDateConfirmation() != null) {
            details.append("Date confirmation : ").append(transaction.getDateConfirmation().toString()).append("\n");
        }
        details.append("Description : ").append(transaction.getDescription() != null ? transaction.getDescription() : "-").append("\n");

        JOptionPane.showMessageDialog(this, details.toString(), "Détails de la Transaction",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void refresh() {
        // Recharger l'utilisateur depuis la base de données
        javax.persistence.EntityManager em = dao.SingletonConnection.getEntityManager();
        Utilisateurs freshUser = em.find(Utilisateurs.class, utilisateurConnecte.getId());
        if (freshUser != null) {
            this.utilisateurConnecte = freshUser;
        }

        // Mettre à jour les labels de solde
        double solde = utilisateurConnecte.getSolde();
        double soldeTemporaire = utilisateurConnecte.getSoldeTemporaire();
        double soldeEffectif = gestionTransaction.calculerSoldeEffectif(utilisateurConnecte.getId());

        lblSolde.setText(String.format("%.2f MAD", solde));
        lblSoldeTemporaire.setText(String.format("%.2f MAD", soldeTemporaire));
        lblSoldeEffectif.setText(String.format("%.2f MAD", soldeEffectif));

        // Charger les transactions
        listeTransactions = gestionTransaction.listerTransactionsParUtilisateur(utilisateurConnecte.getId());

        // Afficher les transactions
        if (txtRecherche.getText().trim().isEmpty()) {
            afficherToutesTransactions();
        } else {
            rechercher();
        }
    }

    // Auto-refresh logic
    private javax.swing.Timer timer;

    @Override
    public void addNotify() {
        super.addNotify();
        if (timer == null) {
            timer = new javax.swing.Timer(5000, e -> refresh());
            timer.start();
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }
}
