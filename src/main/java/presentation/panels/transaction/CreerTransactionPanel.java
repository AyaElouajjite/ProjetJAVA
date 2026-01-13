package presentation.panels.transaction;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ButtonGroup;

import dao.Proprietaire;
import dao.Reparateur;
import dao.TransactionFinanciere;
import dao.TypeTransaction;
import dao.Utilisateurs;
import dao.EtatTransaction;
import metier.GestionBoutique;
import metier.GestionReparateur;
import metier.GestionTransactionFinanciere;
import metier.IGestionTransactionFinanciere;

public class CreerTransactionPanel extends JPanel {

    private JTextField txtMontant;
    private JTextArea txtDescription;
    private JTextField txtNomUtilisateur;
    private JRadioButton radioPret, radioEmprunt;
    private ButtonGroup groupType;
    
    private Utilisateurs utilisateurConnecte; // Peut être Proprietaire ou Reparateur
    private ListerTransactionsPanel listerPanel;
    private IGestionTransactionFinanciere gestionTransaction;
    private metier.GestionUtilisateurs gestionUtilisateurs;

    public CreerTransactionPanel(Utilisateurs utilisateur, ListerTransactionsPanel listerPanel) {
        this.utilisateurConnecte = utilisateur;
        this.listerPanel = listerPanel;
        this.gestionTransaction = new GestionTransactionFinanciere();
        this.gestionUtilisateurs = new metier.GestionUtilisateurs();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Titre
        JLabel lblTitle = new JLabel("Nouvelle Transaction Financière");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(50, 50, 50));
        lblTitle.setHorizontalAlignment(JLabel.CENTER);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        // Formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;

        // Type de transaction
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JLabel lblType = new JLabel("Type de transaction :");
        lblType.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(lblType, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JPanel radioPanel = new JPanel(new java.awt.FlowLayout(FlowLayout.LEFT));
        groupType = new ButtonGroup();
        radioPret = new JRadioButton("Je prête de l'argent", true);
        radioEmprunt = new JRadioButton("J'emprunte de l'argent");
        groupType.add(radioPret);
        groupType.add(radioEmprunt);
        radioPanel.add(radioPret);
        radioPanel.add(radioEmprunt);
        formPanel.add(radioPanel, gbc);

        // Utilisateur cible
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JLabel lblUtilisateur = new JLabel("Avec qui :");
        lblUtilisateur.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(lblUtilisateur, gbc);

        gbc.gridy = 3;
        txtNomUtilisateur = new JTextField();
        JPanel utilisateurPanel = new JPanel(new BorderLayout(5, 5));
        utilisateurPanel.setOpaque(false);
        utilisateurPanel.add(new JLabel("Nom ou prénom de l'utilisateur * :"), BorderLayout.NORTH);
        utilisateurPanel.add(txtNomUtilisateur, BorderLayout.CENTER);
        formPanel.add(utilisateurPanel, gbc);

        // Montant
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        txtMontant = new JTextField();
        JPanel montantPanel = new JPanel(new BorderLayout(5, 5));
        montantPanel.setOpaque(false);
        montantPanel.add(new JLabel("Montant (MAD) * :"), BorderLayout.NORTH);
        montantPanel.add(txtMontant, BorderLayout.CENTER);
        formPanel.add(montantPanel, gbc);

        // Solde disponible
        gbc.gridy = 4;
        gbc.gridx = 1;
        JLabel lblSoldeDispo = new JLabel("Solde disponible : " + String.format("%.2f MAD", 
                gestionTransaction.calculerSoldeEffectif(utilisateurConnecte.getId())));
        lblSoldeDispo.setFont(new Font("Arial", Font.ITALIC, 12));
        lblSoldeDispo.setForeground(Color.GRAY);
        formPanel.add(lblSoldeDispo, gbc);

        // Description
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        txtDescription = new JTextArea(4, 30);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JPanel descPanel = new JPanel(new BorderLayout(5, 5));
        descPanel.setOpaque(false);
        descPanel.add(new JLabel("Description (optionnel) :"), BorderLayout.NORTH);
        descPanel.add(new javax.swing.JScrollPane(txtDescription), BorderLayout.CENTER);
        formPanel.add(descPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Bouton Créer
        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        JButton btnCreer = new JButton("Créer la Transaction");
        btnCreer.setFont(new Font("Arial", Font.BOLD, 14));
        btnCreer.setBackground(new Color(33, 150, 243));
        btnCreer.setForeground(Color.WHITE);
        btnCreer.setFocusPainted(false);
        btnCreer.addActionListener(e -> creerTransaction());

        btnPanel.add(btnCreer);
        add(btnPanel, BorderLayout.SOUTH);
    }


    private void creerTransaction() {
        // Validation du montant
        if (txtMontant.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir un montant", "Erreur",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double montant;
        try {
            montant = Double.parseDouble(txtMontant.getText().trim());
            if (montant <= 0) {
                JOptionPane.showMessageDialog(this, "Le montant doit être positif", "Erreur",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Montant invalide", "Erreur",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validation du nom
        String nomAutrePartie = txtNomUtilisateur.getText().trim();
        if (nomAutrePartie.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir le nom de l'autre partie", "Erreur",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Créer la transaction
        TypeTransaction type = radioPret.isSelected() ? TypeTransaction.PRET : TypeTransaction.EMPRUNT;
        
        TransactionFinanciere.TransactionFinanciereBuilder builder = TransactionFinanciere.builder()
                .montant(montant)
                .typeTransaction(type)
                .etat(EtatTransaction.EN_ATTENTE)
                .description(txtDescription.getText().trim());

        // Si c'est un prêt : l'utilisateur connecté prête à quelqu'un
        if (radioPret.isSelected()) {
            builder.preteur(utilisateurConnecte);
            builder.nomEmprunteurExterne(nomAutrePartie);
        } else {
            // Si c'est un emprunt : l'utilisateur connecté emprunte de quelqu'un
            builder.emprunteur(utilisateurConnecte);
            builder.nomPreteurExterne(nomAutrePartie);
        }

        TransactionFinanciere transaction = builder.build();

        try {
            gestionTransaction.creerTransaction(transaction);
            JOptionPane.showMessageDialog(this, 
                    "✅ Transaction créée avec succès.\nLes ajustements temporaires ont été appliqués.",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);

            if (listerPanel != null) {
                listerPanel.refresh();
            }

            // Réinitialiser le formulaire
            txtMontant.setText("");
            txtDescription.setText("");
            txtNomUtilisateur.setText("");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Erreur : " + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void refresh() {
        // Réinitialiser les champs
        txtNomUtilisateur.setText("");
    }
}
