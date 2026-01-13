package presentation.panels.proprietaire;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import dao.Boutique;
import dao.Proprietaire;
import dao.Reparateur;
import metier.GestionBoutique;

public class TableauDeBordPanel extends JPanel {

    private Proprietaire proprietaire;
    private JLabel lblGlobal, lblPerso;
    private JTable tableBoutiques, tableReparateurs;
    private DefaultTableModel modelBoutiques, modelReparateurs;
    private JTextField txtRechercheBoutique, txtRechercheReparateur;
    
    // Liste pour stocker les données
    private List<Boutique> listeBoutiques;

    public TableauDeBordPanel(Proprietaire p) {
        this.proprietaire = p;
        this.listeBoutiques = new ArrayList<>();
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. STATS EN HAUT
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 0));

        lblGlobal = createStatCard("Caisse Globale (Total)", Color.decode("#4CAF50"));
        lblPerso = createStatCard("Caisse Personnelle (Réparations Propres)", Color.decode("#2196F3"));

        statsPanel.add(lblGlobal);
        statsPanel.add(lblPerso);

        add(statsPanel, BorderLayout.NORTH);

        // 2. LISTES AU CENTRE (GRID LAYOUT 2 LIGNES)
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 20));

        // --- PANEL BOUTIQUES ---
        JPanel panelBoutiques = createBoutiquesPanel();

        // --- PANEL REPARATEURS ---
        JPanel panelReparateurs = createReparateursPanel();

        centerPanel.add(panelBoutiques);
        centerPanel.add(panelReparateurs);

        add(centerPanel, BorderLayout.CENTER);

        refresh();
    }

    private JPanel createBoutiquesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        // Header avec titre et recherche
        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel titre = new JLabel("Détail par Boutique :");
        titre.setFont(new Font("Arial", Font.BOLD, 12));

        // Barre de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.add(new JLabel("Rechercher :"));
        txtRechercheBoutique = new JTextField(15);
        searchPanel.add(txtRechercheBoutique);

        JButton btnRechercherBoutique = new JButton("Rechercher");
        JButton btnAfficherToutBoutique = new JButton("Afficher tout");

        searchPanel.add(btnRechercherBoutique);
        searchPanel.add(btnAfficherToutBoutique);

        // Actions recherche
        btnRechercherBoutique.addActionListener(e -> rechercherBoutique());
        btnAfficherToutBoutique.addActionListener(e -> {
            txtRechercheBoutique.setText("");
            afficherToutesBoutiques();
        });

        // Recherche en temps réel
        txtRechercheBoutique.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                rechercherBoutique();
            }
        });

        headerPanel.add(titre, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        // Table
        String[] colBoutiques = {"ID", "Boutique", "Nombre Réparateurs", "Solde (Commissions)"};
        modelBoutiques = new DefaultTableModel(colBoutiques, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableBoutiques = new JTable(modelBoutiques);
        tableBoutiques.setRowHeight(25);
        tableBoutiques.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(tableBoutiques), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createReparateursPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        // Header avec titre et recherche
        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel titre = new JLabel("Détail par Réparateur :");
        titre.setFont(new Font("Arial", Font.BOLD, 12));

        // Barre de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.add(new JLabel("Rechercher :"));
        txtRechercheReparateur = new JTextField(15);
        searchPanel.add(txtRechercheReparateur);

        JButton btnRechercherReparateur = new JButton("Rechercher");
        JButton btnAfficherToutReparateur = new JButton("Afficher tout");

        searchPanel.add(btnRechercherReparateur);
        searchPanel.add(btnAfficherToutReparateur);

        // Actions recherche
        btnRechercherReparateur.addActionListener(e -> rechercherReparateur());
        btnAfficherToutReparateur.addActionListener(e -> {
            txtRechercheReparateur.setText("");
            afficherTousReparateurs();
        });

        // Recherche en temps réel
        txtRechercheReparateur.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                rechercherReparateur();
            }
        });

        headerPanel.add(titre, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        // Table
        String[] colReparateurs = {"ID", "Nom & Prénom", "Boutique", "Gains (Solde)"};
        modelReparateurs = new DefaultTableModel(colReparateurs, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableReparateurs = new JTable(modelReparateurs);
        tableReparateurs.setRowHeight(25);
        tableReparateurs.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(tableReparateurs), BorderLayout.CENTER);

        return panel;
    }

    private void rechercherBoutique() {
        String recherche = txtRechercheBoutique.getText().trim().toLowerCase();
        modelBoutiques.setRowCount(0);

        for (Boutique b : listeBoutiques) {
            String nomBoutique = (b.getNom() != null) ? b.getNom().toLowerCase() : "";

            if (recherche.isEmpty() || nomBoutique.contains(recherche)) {
                int nbReparateurs = b.getReparateurs() != null ? b.getReparateurs().size() : 0;
                modelBoutiques.addRow(new Object[]{
                        b.getId(),
                        b.getNom(),
                        nbReparateurs,
                        String.format("%.2f MAD", b.getSolde())
                });
            }
        }
    }

    private void afficherToutesBoutiques() {
        modelBoutiques.setRowCount(0);

        for (Boutique b : listeBoutiques) {
            int nbReparateurs = b.getReparateurs() != null ? b.getReparateurs().size() : 0;
            modelBoutiques.addRow(new Object[]{
                    b.getId(),
                    b.getNom(),
                    nbReparateurs,
                    String.format("%.2f MAD", b.getSolde())
            });
        }
    }

    private void rechercherReparateur() {
        String recherche = txtRechercheReparateur.getText().trim().toLowerCase();
        modelReparateurs.setRowCount(0);

        for (Boutique b : listeBoutiques) {
            if (b.getReparateurs() != null) {
                for (Reparateur r : b.getReparateurs()) {
                    String nom = (r.getNom() != null) ? r.getNom().toLowerCase() : "";
                    String prenom = (r.getPrenom() != null) ? r.getPrenom().toLowerCase() : "";
                    String nomComplet = nom + " " + prenom;

                    if (recherche.isEmpty() || nomComplet.contains(recherche)) {
                        modelReparateurs.addRow(new Object[]{
                                r.getId(),
                                r.getNom() + " " + r.getPrenom(),
                                b.getNom(),
                                String.format("%.2f MAD", r.getSolde())
                        });
                    }
                }
            }
        }
    }

    private void afficherTousReparateurs() {
        modelReparateurs.setRowCount(0);

        for (Boutique b : listeBoutiques) {
            if (b.getReparateurs() != null) {
                for (Reparateur r : b.getReparateurs()) {
                    modelReparateurs.addRow(new Object[]{
                            r.getId(),
                            r.getNom() + " " + r.getPrenom(),
                            b.getNom(),
                            String.format("%.2f MAD", r.getSolde())
                    });
                }
            }
        }
    }

    private JLabel createStatCard(String title, Color bgColor) {
        JLabel lbl = new JLabel("0.00 MAD");
        lbl.setFont(new Font("Arial", Font.BOLD, 24));
        lbl.setHorizontalAlignment(JLabel.CENTER);
        lbl.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(bgColor, 2),
                title,
                0, 0, new Font("Arial", Font.BOLD, 12), bgColor));
        return lbl;
    }

    public void refresh() {
        // Reload Proprietor to get fresh Solde
        javax.persistence.EntityManager em = dao.SingletonConnection.getEntityManager();
        Proprietaire freshProp = em.find(Proprietaire.class, proprietaire.getId());
        if (freshProp != null) {
            this.proprietaire = freshProp;
        }

        // Charger les boutiques
        listeBoutiques = new GestionBoutique().listerBoutiquesParProprietaire(proprietaire);

        // Calculer les totaux
        double totalBoutiques = 0.0;
        for (Boutique b : listeBoutiques) {
            totalBoutiques += b.getSolde();
        }

        double soldePerso = proprietaire.getSolde();
        double global = soldePerso + totalBoutiques;

        lblPerso.setText(String.format("%.2f MAD", soldePerso));
        lblGlobal.setText(String.format("%.2f MAD", global));

        // Afficher les données
        afficherToutesBoutiques();
        afficherTousReparateurs();
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