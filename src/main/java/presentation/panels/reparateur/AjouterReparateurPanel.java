package presentation.panels.reparateur;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import dao.Boutique;
import dao.Proprietaire;
import dao.Reparateur;
import metier.GestionBoutique;
import metier.GestionReparateur;

public class AjouterReparateurPanel extends JPanel {

    private JTextField txtNom, txtPrenom, txtCin, txtEmail, txtTel, txtPourcentage;
    private JPasswordField txtMdp;
    private JComboBox<Boutique> comboBoutique;

    private Proprietaire proprietaire;
    private ListerReparateursPanel listerPanel;

    public AjouterReparateurPanel(Proprietaire proprietaire, ListerReparateursPanel listerPanel) {
        this.proprietaire = proprietaire;
        this.listerPanel = listerPanel;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Titre
        JLabel lblTitle = new JLabel("Nouveau Réparateur");
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

        // --- Informations Personnelles ---
        addSectionTitle(formPanel, "Informations Personnelles", gbc, 0);

        gbc.gridy = 1;
        addLabelAndField(formPanel, "Nom *", txtNom = new JTextField(), gbc, 0);
        addLabelAndField(formPanel, "Prénom", txtPrenom = new JTextField(), gbc, 1);

        gbc.gridy = 2;
        addLabelAndField(formPanel, "CIN *", txtCin = new JTextField(), gbc, 0);
        addLabelAndField(formPanel, "Téléphone", txtTel = new JTextField(), gbc, 1);

        gbc.gridy = 3;
        addLabelAndField(formPanel, "Email *", txtEmail = new JTextField(), gbc, 0);

        // --- Compte & Affectation ---
        addSectionTitle(formPanel, "Compte & Affectation", gbc, 4);

        gbc.gridy = 5;
        addLabelAndField(formPanel, "Mot de passe *", txtMdp = new JPasswordField(), gbc, 0);

        comboBoutique = new JComboBox<>();
        updateBoutiqueList();
        addLabelAndComponent(formPanel, "Boutique *", comboBoutique, gbc, 1);

        gbc.gridy = 6;
        txtPourcentage = new JTextField("0.0");
        addLabelAndField(formPanel, "Pourcentage Recrutement (%) *", txtPourcentage, gbc, 0);

        add(formPanel, BorderLayout.CENTER);

        // Bouton Ajouter
        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        JButton btnAjouter = new JButton("Confirmer le Recrutement");
        btnAjouter.setFont(new Font("Arial", Font.BOLD, 14));
        btnAjouter.setBackground(new Color(33, 150, 243));
        btnAjouter.setForeground(Color.WHITE);
        btnAjouter.setFocusPainted(false);
        btnAjouter.addActionListener(e -> ajouter());

        btnPanel.add(btnAjouter);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void addSectionTitle(JPanel panel, String title, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2; // Span across columns
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setForeground(new Color(100, 100, 100));
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(33, 150, 243)));
        panel.add(lbl, gbc);
        gbc.gridwidth = 1; // Reset
    }

    private void addLabelAndField(JPanel panel, String labelText, JTextField field, GridBagConstraints gbc, int col) {
        gbc.gridx = col;
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setOpaque(false);
        p.add(new JLabel(labelText), BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        panel.add(p, gbc);
    }

    private void addLabelAndComponent(JPanel panel, String labelText, javax.swing.JComponent comp,
            GridBagConstraints gbc, int col) {
        gbc.gridx = col;
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setOpaque(false);
        p.add(new JLabel(labelText), BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        panel.add(p, gbc);
    }

    private void ajouter() {
        if (txtNom.getText().trim().isEmpty()
                || txtCin.getText().trim().isEmpty()
                || txtEmail.getText().trim().isEmpty()
                || txtMdp.getPassword().length == 0) {

            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs obligatoires", "Erreur",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Boutique b = (Boutique) comboBoutique.getSelectedItem();
        if (b == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une boutique", "Erreur",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Reparateur r = new Reparateur();
        r.setNom(txtNom.getText().trim());
        r.setPrenom(txtPrenom.getText().trim());
        r.setCIN(txtCin.getText().trim());
        r.setEmail(txtEmail.getText().trim());
        r.setNumTel(txtTel.getText().trim());
        r.setMotDePasse(new String(txtMdp.getPassword()));
        r.setActif(true);

        try {
            r.setPourcentage(Double.parseDouble(txtPourcentage.getText()));
        } catch (NumberFormatException e) {
            r.setPourcentage(0.0);
        }

        try {
            new GestionReparateur().ajouterReparateur(r, b.getId());
            JOptionPane.showMessageDialog(this, "✅ Réparateur ajouté avec succès");

            if (listerPanel != null)
                listerPanel.refresh();

            // Clear fields
            txtNom.setText("");
            txtPrenom.setText("");
            txtCin.setText("");
            txtEmail.setText("");
            txtTel.setText("");
            txtMdp.setText("");
            txtPourcentage.setText("0.0");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Erreur lors de l’ajout", "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public void updateBoutiqueList() {
        comboBoutique.removeAllItems();
        new GestionBoutique().listerBoutiquesParProprietaire(proprietaire).forEach(comboBoutique::addItem);
    }
}
