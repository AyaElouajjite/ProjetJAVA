package presentation.panels.reparateur;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import dao.Boutique;
import dao.Proprietaire;
import dao.Reparateur;
import metier.GestionBoutique;
import metier.GestionReparateur;

public class ListerReparateursPanel extends JPanel {
    private JComboBox<Boutique> comboBoutique;
    private JTable table;
    private DefaultTableModel model;
    private Proprietaire proprietaire;
    private JTextField txtRecherche;
    private GestionReparateur gestionReparateur;
    private GestionBoutique gestionBoutique;

    public ListerReparateursPanel(Proprietaire proprietaire) {
        this.proprietaire = proprietaire;
        this.gestionReparateur = new GestionReparateur();
        this.gestionBoutique = new GestionBoutique();
        setLayout(new BorderLayout(10, 10));

        // 1. D'ABORD initialiser la table et le model
        model = new DefaultTableModel(
                new Object[]{"ID", "Nom", "Prénom", "Email"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(25);

        // 2. ENSUITE initialiser le ComboBox
        comboBoutique = new JComboBox<>();

        // 3. Panel du haut (ComboBox + Recherche)
        JPanel topPanel = new JPanel(new BorderLayout(10, 5));

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
            charger();
        });

        // Recherche en temps réel
        txtRecherche.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                rechercher();
            }
        });

        topPanel.add(comboBoutique, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        // Boutons
        JPanel btnPanel = new JPanel();
        JButton btnSupprimer = new JButton("Supprimer");
        JButton btnModifier = new JButton("Modifier");

        btnSupprimer.addActionListener(e -> supprimer());
        btnModifier.addActionListener(e -> modifier());

        btnPanel.add(btnModifier);
        btnPanel.add(btnSupprimer);

        // Ajout des composants
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        // 4. ENFIN charger les boutiques et ajouter le listener
        updateBoutiqueList();
        comboBoutique.addActionListener(e -> charger());
    }

    private void charger() {
        if (model == null) return;  // Sécurité supplémentaire
        
        model.setRowCount(0);
        Boutique b = (Boutique) comboBoutique.getSelectedItem();
        if (b == null)
            return;

        List<Reparateur> reparateurs = gestionReparateur.listerReparateursParBoutique(b.getId());
        for (Reparateur r : reparateurs) {
            model.addRow(new Object[]{
                    r.getId(),
                    r.getNom(),
                    r.getPrenom(),
                    r.getEmail()
            });
        }
    }

    private void rechercher() {
        if (model == null) return;  // Sécurité supplémentaire
        
        String recherche = txtRecherche.getText().trim().toLowerCase();
        model.setRowCount(0);

        Boutique b = (Boutique) comboBoutique.getSelectedItem();
        if (b == null)
            return;

        List<Reparateur> reparateurs = gestionReparateur.listerReparateursParBoutique(b.getId());
        for (Reparateur r : reparateurs) {
            String nom = (r.getNom() != null) ? r.getNom().toLowerCase() : "";
            String prenom = (r.getPrenom() != null) ? r.getPrenom().toLowerCase() : "";

            if (recherche.isEmpty() || nom.contains(recherche) || prenom.contains(recherche)) {
                model.addRow(new Object[]{
                        r.getId(),
                        r.getNom(),
                        r.getPrenom(),
                        r.getEmail()
                });
            }
        }
    }

    private void supprimer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un réparateur");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Supprimer ce réparateur ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) model.getValueAt(row, 0);
            gestionReparateur.supprimerReparateur(id);
            charger();
            JOptionPane.showMessageDialog(this, "Réparateur supprimé !");
        }
    }

    private void modifier() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un réparateur");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        Reparateur r = gestionReparateur.chercherReparateur(id);

        ModifierReparateurPanel panel = new ModifierReparateurPanel(r, this);
        JOptionPane.showMessageDialog(
                this,
                panel,
                "Modifier Réparateur",
                JOptionPane.PLAIN_MESSAGE);
    }

    public void refresh() {
        charger();
    }

    public void updateBoutiqueList() {
        comboBoutique.removeAllItems();
        for (Boutique b : gestionBoutique.listerBoutiquesParProprietaire(proprietaire)) {
            comboBoutique.addItem(b);
        }

        if (comboBoutique.getItemCount() > 0) {
            comboBoutique.setSelectedIndex(0);
            charger();
        } else {
            if (model != null) {
                model.setRowCount(0);
            }
        }
    }
}