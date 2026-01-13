package presentation.panels.reparateur;

import javax.swing.*;
import java.awt.*;

import dao.Reparateur;

public class ProfilReparateurPanel extends JPanel {

    private JLabel lblNom, lblPrenom, lblEmail, lblTel, lblBoutique, lblPourcentage, lblSolde;
    private Reparateur reparateur;

    public ProfilReparateurPanel(Reparateur r) {
        this.reparateur = r;
        setLayout(new GridLayout(7, 2, 10, 10)); // Increased rows
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        lblNom = new JLabel();
        lblPrenom = new JLabel();
        lblEmail = new JLabel();
        lblTel = new JLabel();
        lblBoutique = new JLabel();
        lblPourcentage = new JLabel();
        lblSolde = new JLabel();

        add(new JLabel("Nom :"));
        add(lblNom);
        add(new JLabel("Prénom :"));
        add(lblPrenom);
        add(new JLabel("Email :"));
        add(lblEmail);
        add(new JLabel("Téléphone :"));
        add(lblTel);
        add(new JLabel("Boutique :"));
        add(lblBoutique);
        add(new JLabel("Pourcentage :"));
        add(lblPourcentage);
        add(new JLabel("💰 SOLDE (Caisse) :"));
        lblSolde.setForeground(new Color(0, 100, 0));
        lblSolde.setFont(new Font("Arial", Font.BOLD, 14));
        add(lblSolde);

        refresh();
    }

    public void refresh() {
        // En théorie, il faudrait recharger depuis la BDD pour avoir le solde à jour
        // Mais pour l'instant on utilise l'objet passé (si mis à jour par référence)
        // Idéalement : reparateur = new
        // GestionReparateur().getById(reparateur.getId());

        lblNom.setText(reparateur.getNom());
        lblPrenom.setText(reparateur.getPrenom());
        lblEmail.setText(reparateur.getEmail());
        lblTel.setText(reparateur.getNumTel());
        lblBoutique.setText(reparateur.getBoutique() != null ? reparateur.getBoutique().getNom() : "-");
        lblPourcentage.setText(reparateur.getPourcentage() + "%");
        lblSolde.setText(String.format("%.2f MAD", reparateur.getSolde()));
    }
}
