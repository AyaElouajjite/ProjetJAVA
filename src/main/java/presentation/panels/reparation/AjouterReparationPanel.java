package presentation.panels.reparation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import dao.AppareilReparation;
import dao.Boutique;
import dao.Client;
import dao.Proprietaire;
import dao.Reparateur;
import dao.Reparation;
import dao.TypeAppareil;
import metier.GestionBoutique;
import metier.GestionClient;
import metier.GestionReparation;
import metier.GestionTypeAppareil;

public class AjouterReparationPanel extends JPanel {

    // Global Fields
    private JComboBox<Client> comboClient;
    private JComboBox<Boutique> comboBoutique;

    // New Client Fields
    private JCheckBox chkNouveauClient;
    private JPanel panelNouveauClient;
    private JTextField txtNomClient, txtPrenomClient, txtTelClient;

    // Device Fields (Input)
    private JComboBox<TypeAppareil> comboType;
    private JTextField txtImei, txtMontantAppareil;
    private JTextArea txtDescAppareil;

    // Device List (Temporary)
    private JTable tableAppareils;
    private DefaultTableModel modelAppareils;
    private List<AppareilReparation> listeAppareilsTemp = new ArrayList<>();
    private JLabel lblTotal;

    private Proprietaire proprietaire;
    private Reparateur reparateur;
    private GestionReparationsPanel parentPanel;

    public AjouterReparationPanel(Proprietaire p, Reparateur r, GestionReparationsPanel parent) {
        this.proprietaire = p;
        this.reparateur = r;
        this.parentPanel = parent;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // === NORTH: TICKET INFO (Client, Boutique) ===
        JPanel headerPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        headerPanel.setBorder(BorderFactory.createTitledBorder("Informations Générales"));

        // Boutique
        comboBoutique = new JComboBox<>();
        chargerBoutiques();
        headerPanel.add(new JLabel("Boutique *"));
        headerPanel.add(comboBoutique);

        // Client
        comboClient = new JComboBox<>();
        chargerClients();
        chkNouveauClient = new JCheckBox("Nouveau Client ?");

        JPanel clientPanel = new JPanel(new BorderLayout());
        clientPanel.add(comboClient, BorderLayout.CENTER);
        clientPanel.add(chkNouveauClient, BorderLayout.SOUTH);

        headerPanel.add(new JLabel("Client *"));
        headerPanel.add(clientPanel);

        // New Client Sub-form
        panelNouveauClient = new JPanel(new GridLayout(3, 2));
        txtNomClient = new JTextField();
        txtPrenomClient = new JTextField();
        txtTelClient = new JTextField();
        panelNouveauClient.add(new JLabel("Nom *"));
        panelNouveauClient.add(txtNomClient);
        panelNouveauClient.add(new JLabel("Prénom"));
        panelNouveauClient.add(txtPrenomClient);
        panelNouveauClient.add(new JLabel("Tél *"));
        panelNouveauClient.add(txtTelClient);
        panelNouveauClient.setVisible(false);

        // Wrapper for Header
        JPanel northWrapper = new JPanel(new BorderLayout());
        northWrapper.add(headerPanel, BorderLayout.CENTER);
        northWrapper.add(panelNouveauClient, BorderLayout.SOUTH);

        add(northWrapper, BorderLayout.NORTH);

        // === CENTER: DEVICES LIST & ADD FORM ===
        JPanel devicesPanel = new JPanel(new BorderLayout(10, 10));
        devicesPanel.setBorder(BorderFactory.createTitledBorder("Appareils à Réparer"));

        // -- Input Form --
        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));

        comboType = new JComboBox<>();
        chargerTypesAppareil();
        JButton btnAddType = new JButton("+");
        btnAddType.addActionListener(e -> ajouterTypeDynamique());
        JPanel typeWrapper = new JPanel(new BorderLayout());
        typeWrapper.add(comboType, BorderLayout.CENTER);
        typeWrapper.add(btnAddType, BorderLayout.EAST);

        txtImei = new JTextField();
        txtMontantAppareil = new JTextField();
        txtDescAppareil = new JTextArea(2, 20);

        inputPanel.add(new JLabel("Type Appareil"));
        inputPanel.add(typeWrapper);
        inputPanel.add(new JLabel("IMEI"));
        inputPanel.add(txtImei);
        inputPanel.add(new JLabel("Prix Estimé"));
        inputPanel.add(txtMontantAppareil);
        inputPanel.add(new JLabel("Panne / Desc."));
        inputPanel.add(new JScrollPane(txtDescAppareil));

        JButton btnAjouterAppareil = new JButton("Ajouter cet appareil au ticket");
        btnAjouterAppareil.addActionListener(e -> ajouterAppareilALaListe());

        JPanel inputWrapper = new JPanel(new BorderLayout());
        inputWrapper.add(inputPanel, BorderLayout.CENTER);
        inputWrapper.add(btnAjouterAppareil, BorderLayout.SOUTH);

        // -- Table --
        modelAppareils = new DefaultTableModel(new String[] { "Type", "IMEI", "Description", "Prix" }, 0);
        tableAppareils = new JTable(modelAppareils);
        tableAppareils.setPreferredScrollableViewportSize(new Dimension(400, 150));

        devicesPanel.add(inputWrapper, BorderLayout.NORTH);
        devicesPanel.add(new JScrollPane(tableAppareils), BorderLayout.CENTER);

        // -- Total --
        lblTotal = new JLabel("Total: 0.0 MAD");
        lblTotal.setFont(lblTotal.getFont().deriveFont(java.awt.Font.BOLD, 16));
        devicesPanel.add(lblTotal, BorderLayout.SOUTH);

        add(devicesPanel, BorderLayout.CENTER);

        // === SOUTH: VALIDATE ===
        JButton btnValider = new JButton("CONFIRMER TICKET RÉPARATION");
        btnValider.setFont(btnValider.getFont().deriveFont(java.awt.Font.BOLD, 18));
        btnValider.addActionListener(e -> enregistrerTicket());
        add(btnValider, BorderLayout.SOUTH);

        // Events
        chkNouveauClient.addActionListener(e -> {
            boolean isNew = chkNouveauClient.isSelected();
            comboClient.setEnabled(!isNew);
            panelNouveauClient.setVisible(isNew);
            revalidate();
        });
    }

    // --- LOGIC ---

    private void ajouterAppareilALaListe() {
        TypeAppareil type = (TypeAppareil) comboType.getSelectedItem();
        if (type == null)
            return;

        String imei = txtImei.getText();
        String desc = txtDescAppareil.getText();
        double prix = 0.0;
        try {
            prix = Double.parseDouble(txtMontantAppareil.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Prix invalide");
            return;
        }

        AppareilReparation ap = new AppareilReparation();
        ap.setTypeAppareil(type);
        ap.setImei(imei);
        ap.setDescription(desc);
        ap.setMontant(prix);

        listeAppareilsTemp.add(ap);
        modelAppareils.addRow(new Object[] { type.getNom(), imei, desc, prix });

        updateTotal();

        // Reset inputs
        txtImei.setText("");
        txtDescAppareil.setText("");
        txtMontantAppareil.setText("");
    }

    private void updateTotal() {
        double t = 0;
        for (AppareilReparation ap : listeAppareilsTemp)
            t += ap.getMontant();
        lblTotal.setText("Total: " + t + " MAD");
    }

    private void enregistrerTicket() {
        if (listeAppareilsTemp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ajoutez au moins un appareil !");
            return;
        }

        Boutique b = (Boutique) comboBoutique.getSelectedItem();
        if (b == null)
            return;

        // Client Handling
        Client clientFinal = null;
        if (chkNouveauClient.isSelected()) {
            String nom = txtNomClient.getText();
            String tel = txtTelClient.getText();
            if (nom.isEmpty() || tel.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nom et Tél obligatoires pour nouveau client");
                return;
            }
            Client newC = new Client();
            newC.setNom(nom);
            newC.setPrenom(txtPrenomClient.getText());
            newC.setTelephone(tel);
            try {
                new GestionClient().ajouterClient(newC);
                clientFinal = newC;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur client: " + ex.getMessage());
                return;
            }
        } else {
            clientFinal = (Client) comboClient.getSelectedItem();
        }

        Reparation r = new Reparation();
        r.setBoutique(b);
        r.setClient(clientFinal);
        r.setReparateur(reparateur);
        r.setAppareils(new ArrayList<>(listeAppareilsTemp)); // Copy list

        try {
            new GestionReparation().ajouterReparation(r);
            JOptionPane.showMessageDialog(this, "Ticket enregistré avec succès !");
            resetForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage());
        }
    }

    private void resetForm() {
        listeAppareilsTemp.clear();
        modelAppareils.setRowCount(0);
        updateTotal();
        txtNomClient.setText("");
        txtTelClient.setText("");
        if (chkNouveauClient.isSelected()) {
            refresh();
            chkNouveauClient.setSelected(false);
            panelNouveauClient.setVisible(false);
            comboClient.setEnabled(true);
        }
    }

    public void refresh() {
        chargerBoutiques();
        chargerClients();
        chargerTypesAppareil();
    }

    private void chargerTypesAppareil() {
        comboType.removeAllItems();
        List<TypeAppareil> types = new GestionTypeAppareil().listerTypesAppareil();
        for (TypeAppareil t : types)
            comboType.addItem(t);
    }

    private void chargerClients() {
        comboClient.removeAllItems();
        List<Client> clients = new GestionClient().listerClients();
        for (Client c : clients)
            comboClient.addItem(c);
    }

    private void chargerBoutiques() {
        comboBoutique.removeAllItems();
        if (proprietaire != null) {
            List<Boutique> boutiques = new GestionBoutique().listerBoutiquesParProprietaire(proprietaire);
            for (Boutique b : boutiques)
                comboBoutique.addItem(b);
        } else if (reparateur != null && reparateur.getBoutique() != null) {
            comboBoutique.addItem(reparateur.getBoutique());
            comboBoutique.setEnabled(false);
        }
    }

    private void ajouterTypeDynamique() {
        String nouveauNom = JOptionPane.showInputDialog(this, "Nouveau Type d'Appareil :");
        if (nouveauNom != null && !nouveauNom.trim().isEmpty()) {
            TypeAppareil t = new TypeAppareil();
            t.setNom(nouveauNom.trim());

            try {
                new GestionTypeAppareil().ajouterTypeAppareil(t);
                chargerTypesAppareil();
                // Sélectionner le nouveau
                for (int i = 0; i < comboType.getItemCount(); i++) {
                    if (comboType.getItemAt(i).getNom().equals(nouveauNom.trim())) {
                        comboType.setSelectedIndex(i);
                        break;
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
            }
        }
    }
}
