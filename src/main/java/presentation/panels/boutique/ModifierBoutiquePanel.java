package presentation.panels.boutique;

import javax.swing.*;
import java.awt.*;

import dao.Boutique;
import metier.GestionBoutique;

public class ModifierBoutiquePanel extends JPanel {

    private JTextField txtNom, txtAdresse, txtTel, txtPatente;
    private Boutique boutique;
    private ListerBoutiquesPanel parent;

    public ModifierBoutiquePanel(Boutique boutique,
                                 ListerBoutiquesPanel parent) {

        this.boutique = boutique;
        this.parent = parent;

        setLayout(new GridLayout(5, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtNom = new JTextField(boutique.getNom());
        txtAdresse = new JTextField(boutique.getAdresse());
        txtTel = new JTextField(boutique.getNumTel());
        txtPatente = new JTextField(boutique.getPatente());

        add(new JLabel("Nom"));
        add(txtNom);

        add(new JLabel("Adresse"));
        add(txtAdresse);

        add(new JLabel("Téléphone"));
        add(txtTel);

        add(new JLabel("Patente"));
        add(txtPatente);

        JButton btnSave = new JButton("Enregistrer");
        btnSave.addActionListener(e -> enregistrer());

        add(new JLabel());
        add(btnSave);
    }

    private void enregistrer() {
        boutique.setNom(txtNom.getText());
        boutique.setAdresse(txtAdresse.getText());
        boutique.setNumTel(txtTel.getText());
        boutique.setPatente(txtPatente.getText());

        new GestionBoutique().modifierBoutique(boutique);

        JOptionPane.showMessageDialog(this,
                "Boutique modifiée avec succès");

        parent.refresh();
        SwingUtilities.getWindowAncestor(this).dispose();
    }
}
