package presentation.panels.reparateur;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import dao.Reparateur;
import dao.Reparation;
import metier.GestionReparation;

import java.util.ArrayList;
import java.util.List;

public class TableauDeBordReparateurPanel extends JPanel {

    private Reparateur reparateur;
    private JLabel lblSolde, lblCommission;
    private JTable tableReparations;
    private DefaultTableModel model;
    private JTextField txtRecherche;
    private List<Reparation> listeReparations;

    public TableauDeBordReparateurPanel(Reparateur r) {
        this.reparateur = r;
        this.listeReparations = new ArrayList<>();
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. STATS (Cartes colorées)
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 0));

        lblSolde = createStatCard("💰 Mon Solde Actuel", Color.decode("#4CAF50"));
        lblCommission = createStatCard("📊 Ma Part (Reste %)", Color.decode("#FF9800"));

        statsPanel.add(lblSolde);
        statsPanel.add(lblCommission);

        add(statsPanel, BorderLayout.NORTH);

        // 2. Panel des réparations
        JPanel listPanel = new JPanel(new BorderLayout(5, 5));

        // Header avec titre et recherche
        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel titre = new JLabel("Mes Dernières Réparations :");
        titre.setFont(new Font("Arial", Font.BOLD, 12));

        // Barre de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.add(new JLabel("Rechercher par client :"));
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
            afficherToutesReparations();
        });

        // Recherche en temps réel
        txtRecherche.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                rechercher();
            }
        });

        headerPanel.add(titre, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        // Table avec colonne Client ajoutée
        String[] colonnes = {"Code", "Client", "Appareil", "Date", "Montant", "Etat"};
        model = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableReparations = new JTable(model);
        tableReparations.setRowHeight(25);
        tableReparations.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        listPanel.add(headerPanel, BorderLayout.NORTH);
        listPanel.add(new JScrollPane(tableReparations), BorderLayout.CENTER);

        add(listPanel, BorderLayout.CENTER);

        refresh();
    }

    private JLabel createStatCard(String title, Color bgColor) {
        JLabel lbl = new JLabel("...");
        lbl.setFont(new Font("Arial", Font.BOLD, 24));
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

        for (Reparation r : listeReparations) {
            String nomClient = "";
            if (r.getClient() != null) {
                String nom = (r.getClient().getNom() != null) ? r.getClient().getNom().toLowerCase() : "";
                String prenom = (r.getClient().getPrenom() != null) ? r.getClient().getPrenom().toLowerCase() : "";
                nomClient = nom + " " + prenom;
            }

            if (recherche.isEmpty() || nomClient.contains(recherche)) {
                ajouterLigneTable(r);
            }
        }
    }

    private void afficherToutesReparations() {
        model.setRowCount(0);
        for (Reparation r : listeReparations) {
            ajouterLigneTable(r);
        }
    }

    private void ajouterLigneTable(Reparation r) {
        String clientNom = "?";
        if (r.getClient() != null) {
            clientNom = r.getClient().getNom() + " " + (r.getClient().getPrenom() != null ? r.getClient().getPrenom() : "");
        }

        String appareils = "Aucun";
        if (r.getAppareils() != null && !r.getAppareils().isEmpty()) {
            appareils = r.getAppareils().stream()
                    .map(ap -> (ap.getTypeAppareil() != null ? ap.getTypeAppareil().getNom() : ""))
                    .collect(java.util.stream.Collectors.joining(", "));
        }

        model.addRow(new Object[]{
                r.getCodeUnique(),
                clientNom,
                appareils,
                r.getDateCreation(),
                r.getMontant() + " MAD",
                r.getEtat()
        });
    }

    public void refresh() {
        // Reload Reparateur from DB to get fresh Solde
        this.reparateur = new metier.GestionReparateur().chercherReparateur(reparateur.getId());

        // Mettre à jour les labels
        lblSolde.setText(String.format("%.2f MAD", reparateur.getSolde()));

        Double partBoutique = reparateur.getPourcentage();
        double maPart = 100.0 - (partBoutique != null ? partBoutique : 0.0);
        lblCommission.setText(String.format("%.0f %%", maPart));

        // Charger les réparations
        listeReparations = new GestionReparation().listerReparationsParReparateur(reparateur.getId());

        // Vérifier si une recherche est en cours
        if (txtRecherche.getText().trim().isEmpty()) {
            afficherToutesReparations();
        } else {
            rechercher();
        }
    }

    // --- AUTO-REFRESH LOGIC ---
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