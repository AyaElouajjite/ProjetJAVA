package presentation.panels.boutique;

import javax.swing.*;
import java.awt.*;

import dao.Boutique;
import dao.Proprietaire;
import metier.GestionBoutique;

public class CreerBoutiquePanel extends JPanel {

    private JTextField txtNom, txtAdresse, txtTel, txtPatente;
    private Proprietaire proprietaire;
    private ListerBoutiquesPanel listerPanel; // Link

    public CreerBoutiquePanel(Proprietaire proprietaire, ListerBoutiquesPanel listerPanel) {
        this.proprietaire = proprietaire;
        this.listerPanel = listerPanel;

        setLayout(new GridLayout(5, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        txtNom = new JTextField();
        txtAdresse = new JTextField();
        txtTel = new JTextField();
        txtPatente = new JTextField();

        add(new JLabel("Nom de la boutique"));
        add(txtNom);

        add(new JLabel("Adresse"));
        add(txtAdresse);

        add(new JLabel("Téléphone"));
        add(txtTel);

        add(new JLabel("Patente"));
        add(txtPatente);

        JButton btnAjouter = new JButton("Créer la boutique");
        btnAjouter.addActionListener(e -> creer());

        add(new JLabel());
        add(btnAjouter);
    }

    private void creer() {
        if (txtNom.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Le nom est obligatoire");
            return;
        }

        Boutique b = new Boutique();
        b.setNom(txtNom.getText());
        b.setAdresse(txtAdresse.getText());
        b.setNumTel(txtTel.getText());
        b.setPatente(txtPatente.getText());
        b.setProprietaire(proprietaire);

        new GestionBoutique().ajouterBoutique(b);

        JOptionPane.showMessageDialog(this,
                "Boutique créée avec succès");

        if (listerPanel != null)
            listerPanel.refresh(); // 🔁 Update list

        txtNom.setText("");
        txtAdresse.setText("");
        txtTel.setText("");
        txtPatente.setText("");
    }
}
