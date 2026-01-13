package presentation.panels.boutique;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import dao.Boutique;
import dao.Proprietaire;
import metier.GestionBoutique;

public class ListerBoutiquesPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private Proprietaire proprietaire;
    private JTextField txtRecherche;
    private GestionBoutique gestionBoutique;

    public ListerBoutiquesPanel(Proprietaire proprietaire) {
        this.proprietaire = proprietaire;
        this.gestionBoutique = new GestionBoutique();
        setLayout(new BorderLayout(10, 10));

        // Barre de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        searchPanel.add(new JLabel("Rechercher par nom :"));
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
            charger();
        });

        // Recherche en temps réel
        txtRecherche.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                rechercher();
            }
        });

        // Table
        model = new DefaultTableModel(
                new Object[]{"ID", "Nom", "Adresse", "Téléphone"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(25);

        // Boutons
        JPanel btnPanel = new JPanel();
        JButton btnSupprimer = new JButton("Supprimer");
        JButton btnModifier = new JButton("Modifier");

        btnSupprimer.addActionListener(e -> supprimer());
        btnModifier.addActionListener(e -> modifier());

        btnPanel.add(btnModifier);
        btnPanel.add(btnSupprimer);

        // Ajout des composants
        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        charger();
    }

    private void charger() {
        model.setRowCount(0);
        List<Boutique> boutiques = gestionBoutique.listerBoutiquesParProprietaire(proprietaire);
        for (Boutique b : boutiques) {
            model.addRow(new Object[]{
                    b.getId(),
                    b.getNom(),
                    b.getAdresse(),
                    b.getNumTel()
            });
        }
    }

    private void rechercher() {
        String recherche = txtRecherche.getText().trim().toLowerCase();
        model.setRowCount(0);

        List<Boutique> boutiques = gestionBoutique.listerBoutiquesParProprietaire(proprietaire);
        for (Boutique b : boutiques) {
            String nomBoutique = (b.getNom() != null) ? b.getNom().toLowerCase() : "";

            if (recherche.isEmpty() || nomBoutique.contains(recherche)) {
                model.addRow(new Object[]{
                        b.getId(),
                        b.getNom(),
                        b.getAdresse(),
                        b.getNumTel()
                });
            }
        }
    }

    private void supprimer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une boutique");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Supprimer cette boutique ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) model.getValueAt(row, 0);
            gestionBoutique.supprimerBoutique(id);
            charger();
            JOptionPane.showMessageDialog(this, "Boutique supprimée !");
        }
    }

    private void modifier() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une boutique");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        Boutique b = gestionBoutique.chercherBoutique(id);

        ModifierBoutiquePanel panel = new ModifierBoutiquePanel(b, this);
        JOptionPane.showMessageDialog(this, panel, "Modifier Boutique",
                JOptionPane.PLAIN_MESSAGE);
    }

    public void refresh() {
        charger();
    }
}