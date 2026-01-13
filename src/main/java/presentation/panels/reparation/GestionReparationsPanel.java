package presentation.panels.reparation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import dao.AppareilReparation;
import dao.EtatReparation;
import dao.Proprietaire;
import dao.Reparateur;
import dao.Reparation;
import metier.GestionReparation;

public class GestionReparationsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private GestionReparation service;

    private Proprietaire proprietaire;
    private Reparateur reparateur;

    // Filtres
    private JComboBox<EtatReparation> comboEtat;
    private JTextField txtRecherche;
    private JButton btnRafraichir;
    private JButton btnAjouter;
    private JButton btnDetails;
    private JButton btnSupprimer;

    public GestionReparationsPanel(Proprietaire p, Reparateur r) {
        this.proprietaire = p;
        this.reparateur = r;
        this.service = new GestionReparation();

        setLayout(new BorderLayout(10, 10));

        // Panel du haut (Filtres + Recherche)
        JPanel topContainer = new JPanel(new BorderLayout(10, 5));

        // 1. Barre d'outils (Boutons)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        comboEtat = new JComboBox<>(EtatReparation.values());
        comboEtat.insertItemAt(null, 0);
        comboEtat.setSelectedIndex(0);

        btnRafraichir = new JButton("Actualiser");
        btnAjouter = new JButton("Nouvelle Réparation");
        btnDetails = new JButton("Détails / Traitement");
        btnSupprimer = new JButton("Supprimer");

        topPanel.add(new JLabel("Filtre État:"));
        topPanel.add(comboEtat);
        topPanel.add(btnRafraichir);
        topPanel.add(btnAjouter);
        topPanel.add(btnDetails);
        topPanel.add(btnSupprimer);

        // 2. Barre de recherche
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
            chargerDonnees();
        });

        // Recherche en temps réel
        txtRecherche.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                rechercher();
            }
        });

        topContainer.add(topPanel, BorderLayout.NORTH);
        topContainer.add(searchPanel, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);

        // 3. Table (Centre)
        String[] colonnes = {"ID", "Code", "Date", "Appareils", "Client", "Boutique", "Montant", "État Global"};
        model = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(25);

        // Custom renderer pour les états (Couleurs)
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 7) {
                    String etat = value.toString();
                    if (etat.equals("EN_ATTENTE"))
                        c.setForeground(Color.RED);
                    else if (etat.equals("EN_COURS"))
                        c.setForeground(Color.ORANGE);
                    else if (etat.equals("TERMINE"))
                        c.setForeground(Color.BLUE);
                    else if (etat.equals("LIVRE"))
                        c.setForeground(new Color(0, 100, 0));
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Events
        btnRafraichir.addActionListener(e -> chargerDonnees());
        comboEtat.addActionListener(e -> chargerDonnees());
        btnSupprimer.addActionListener(e -> supprimer());
        btnDetails.addActionListener(e -> ouvrirDetails());

        // Initialisation
        chargerDonnees();
    }

    public void chargerDonnees() {
        model.setRowCount(0);
        List<Reparation> liste = getListeReparations();

        EtatReparation filtre = (EtatReparation) comboEtat.getSelectedItem();

        for (Reparation r : liste) {
            if (filtre != null && r.getEtat() != filtre)
                continue;

            ajouterLigneTable(r);
        }
    }

    private void rechercher() {
        String recherche = txtRecherche.getText().trim().toLowerCase();
        model.setRowCount(0);

        List<Reparation> liste = getListeReparations();
        EtatReparation filtre = (EtatReparation) comboEtat.getSelectedItem();

        for (Reparation r : liste) {
            // Filtre par état
            if (filtre != null && r.getEtat() != filtre)
                continue;

            // Filtre par client
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

    private List<Reparation> getListeReparations() {
        if (proprietaire != null) {
            return service.listerToutesReparations();
        } else if (reparateur != null) {
            return service.listerReparationsParReparateur(reparateur.getId());
        }
        return List.of();
    }

    private void ajouterLigneTable(Reparation r) {
        String appareilsSummary = "?";
        if (r.getAppareils() != null && !r.getAppareils().isEmpty()) {
            appareilsSummary = r.getAppareils().stream()
                    .map(ap -> (ap.getTypeAppareil() != null ? ap.getTypeAppareil().getNom() : "Inconnu"))
                    .collect(Collectors.joining(", "));
        }

        model.addRow(new Object[]{
                r.getId(),
                r.getCodeUnique(),
                r.getDateCreation(),
                appareilsSummary,
                (r.getClient() != null ? r.getClient().toString() : "?"),
                (r.getBoutique() != null ? r.getBoutique().getNom() : "?"),
                r.getMontant() + " MAD",
                r.getEtat()
        });
    }

    private void supprimer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une réparation");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Supprimer cette réparation et tous ses appareils ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            service.supprimerReparation(id);
            chargerDonnees();
            JOptionPane.showMessageDialog(this, "Réparation supprimée !");
        }
    }

    private void ouvrirDetails() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une réparation");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        Reparation r = service.getReparation(id);

        DetailReparationDialog dialog = new DetailReparationDialog(r, service, this);
        dialog.setVisible(true);
    }

    public JButton getBtnAjouter() {
        return btnAjouter;
    }

    // --- INNER CLASS DIALOG ---
    class DetailReparationDialog extends JDialog {
        private JTable tableAppareils;
        private DefaultTableModel modelAppareils;
        private GestionReparation service;
        private Reparation reparation;
        private GestionReparationsPanel parentPanel;

        public DetailReparationDialog(Reparation r, GestionReparation s, GestionReparationsPanel parent) {
            super((javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(parent),
                    "Détails Réparation " + r.getCodeUnique(), true);
            this.reparation = r;
            this.service = s;
            this.parentPanel = parent;

            setSize(800, 500);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            // Header Info
            JPanel infoPanel = new JPanel(new java.awt.GridLayout(0, 2));
            infoPanel.setBorder(BorderFactory.createTitledBorder("Infos Ticket"));
            infoPanel.add(new JLabel("Client: " + (r.getClient() != null ? r.getClient().toString() : "")));
            infoPanel.add(new JLabel("Etat Global: " + r.getEtat()));
            infoPanel.add(new JLabel("Montant Total: " + r.getMontant()));
            add(infoPanel, BorderLayout.NORTH);

            // Devices Table
            String[] cols = {"ID", "Type", "IMEI", "Panne", "Prix", "État Actuel"};
            modelAppareils = new DefaultTableModel(cols, 0);
            tableAppareils = new JTable(modelAppareils);
            loadDevices();
            add(new JScrollPane(tableAppareils), BorderLayout.CENTER);

            // Actions
            JPanel actionsPanel = new JPanel();
            JButton btnChangeStatus = new JButton("Changer État Appareil Sélectionné");
            btnChangeStatus.addActionListener(e -> changeStatusAction());
            actionsPanel.add(btnChangeStatus);

            add(actionsPanel, BorderLayout.SOUTH);
        }

        private void loadDevices() {
            modelAppareils.setRowCount(0);
            reparation = service.getReparation(reparation.getId());
            for (AppareilReparation ap : reparation.getAppareils()) {
                modelAppareils.addRow(new Object[]{
                        ap.getId(),
                        (ap.getTypeAppareil() != null ? ap.getTypeAppareil().getNom() : "?"),
                        ap.getImei(),
                        ap.getDescription(),
                        ap.getMontant(),
                        ap.getEtat()
                });
            }
        }

        private void changeStatusAction() {
            int row = tableAppareils.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Sélectionnez un appareil");
                return;
            }
            Long idLong = (Long) modelAppareils.getValueAt(row, 0);
            int apId = idLong.intValue();

            EtatReparation current = (EtatReparation) modelAppareils.getValueAt(row, 5);

            EtatReparation[] etats = EtatReparation.values();
            EtatReparation startSel = current != null ? current : EtatReparation.EN_ATTENTE;

            Object choix = JOptionPane.showInputDialog(this, "Nouvel état pour cet appareil :", "Changer État",
                    JOptionPane.QUESTION_MESSAGE, null, etats, startSel);

            if (choix != null) {
                EtatReparation newEtat = (EtatReparation) choix;
                service.changerEtatAppareil(apId, newEtat);
                loadDevices();
                parentPanel.chargerDonnees();
            }
        }
    }
}