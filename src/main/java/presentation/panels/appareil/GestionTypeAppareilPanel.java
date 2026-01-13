package presentation.panels.appareil;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import dao.TypeAppareil;
import metier.GestionTypeAppareil;
import java.util.List;

public class GestionTypeAppareilPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private GestionTypeAppareil service;
    private JTextField txtRecherche;

    public GestionTypeAppareilPanel() {
        this.service = new GestionTypeAppareil();
        setLayout(new BorderLayout(10, 10));

        // Panel du haut (Boutons + Recherche)
        JPanel topPanel = new JPanel(new BorderLayout(10, 5));

        // Boutons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAjouter = new JButton("Ajouter Type");
        JButton btnSupprimer = new JButton("Supprimer");
        btnPanel.add(btnAjouter);
        btnPanel.add(btnSupprimer);

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

        topPanel.add(btnPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // TABLE
        String[] colonnes = {"ID", "Nom"};
        model = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // EVENTS
        btnAjouter.addActionListener(e -> ajouter());
        btnSupprimer.addActionListener(e -> supprimer());

        charger();
    }

    private void charger() {
        model.setRowCount(0);
        List<TypeAppareil> types = service.listerTypesAppareil();
        for (TypeAppareil t : types) {
            model.addRow(new Object[]{t.getId(), t.getNom()});
        }
    }

    private void rechercher() {
        String recherche = txtRecherche.getText().trim().toLowerCase();
        model.setRowCount(0);

        List<TypeAppareil> types = service.listerTypesAppareil();
        for (TypeAppareil t : types) {
            String nom = (t.getNom() != null) ? t.getNom().toLowerCase() : "";

            if (recherche.isEmpty() || nom.contains(recherche)) {
                model.addRow(new Object[]{t.getId(), t.getNom()});
            }
        }
    }

    private void ajouter() {
        String nom = JOptionPane.showInputDialog(this, "Nom du type d'appareil :");
        if (nom != null && !nom.trim().isEmpty()) {
            try {
                TypeAppareil t = new TypeAppareil();
                t.setNom(nom.trim());
                service.ajouterTypeAppareil(t);
                charger();
                JOptionPane.showMessageDialog(this, "Type ajouté !");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur (Doublon ?) : " + e.getMessage());
            }
        }
    }

    private void supprimer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un type");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Supprimer ce type ? (Attention si utilisé !)",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.supprimerTypeAppareil(id);
                charger();
                JOptionPane.showMessageDialog(this, "Type supprimé !");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Impossible de supprimer (Probablement utilisé)");
            }
        }
    }
}