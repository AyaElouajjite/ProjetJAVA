package presentation.panels.reparateur;

import javax.swing.*;
import java.awt.*;

import dao.Reparateur;
import metier.GestionReparateur;

public class ModifierReparateurPanel extends JPanel {

    private JTextField txtNom, txtPrenom, txtCin, txtEmail, txtTel, txtPourcentage;
    private Reparateur reparateur;
    private ListerReparateursPanel parent;

    public ModifierReparateurPanel(Reparateur reparateur,
            ListerReparateursPanel parent) {

        this.reparateur = reparateur;
        this.parent = parent;

        setLayout(new GridLayout(7, 2, 10, 10)); // Ajusté pour plus de champs
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Initialisation avec valeurs existantes
        txtNom = new JTextField(reparateur.getNom());
        txtPrenom = new JTextField(reparateur.getPrenom());
        txtCin = new JTextField(reparateur.getCIN());
        txtEmail = new JTextField(reparateur.getEmail());
        txtTel = new JTextField(reparateur.getNumTel());

        Double p = reparateur.getPourcentage();
        txtPourcentage = new JTextField(p != null ? String.valueOf(p) : "0.0");

        add(new JLabel("Nom"));
        add(txtNom);
        add(new JLabel("Prénom"));
        add(txtPrenom);
        add(new JLabel("CIN"));
        add(txtCin);
        add(new JLabel("Email"));
        add(txtEmail);
        add(new JLabel("Téléphone"));
        add(txtTel);
        add(new JLabel("Pourcentage (%)"));
        add(txtPourcentage);

        JButton btnSave = new JButton("Enregistrer les modifications");
        btnSave.addActionListener(e -> enregistrer());

        add(new JLabel());
        add(btnSave);
    }

    private void enregistrer() {
        if (txtNom.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nom et Email obligatoires");
            return;
        }

        reparateur.setNom(txtNom.getText().trim());
        reparateur.setPrenom(txtPrenom.getText().trim());
        reparateur.setCIN(txtCin.getText().trim());
        reparateur.setEmail(txtEmail.getText().trim());
        reparateur.setNumTel(txtTel.getText().trim());

        try {
            reparateur.setPourcentage(Double.parseDouble(txtPourcentage.getText()));
        } catch (NumberFormatException e) {
            reparateur.setPourcentage(0.0);
        }

        try {
            new GestionReparateur().modifierReparateur(reparateur);
            JOptionPane.showMessageDialog(this, "Réparateur modifié avec succès ✅");

            parent.refresh(); // Important : Rafraichir la liste parente

            // Fermer la fenêtre de dialogue qui contient ce panel
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w != null)
                w.dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }
}
