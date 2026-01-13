package presentation.panels.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import dao.Client;
import metier.GestionClient;

public class GestionClientsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private GestionClient gestionClient;

    private JTextField txtNom, txtPrenom, txtTel, txtEmail;
    private JTextField txtRecherche;
    private JLabel lblPhotoPreview;
    private String cheminPhoto = "";
    private int selectedClientId = -1;

    public GestionClientsPanel() {
        this.gestionClient = new GestionClient();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel principal : formulaire à gauche, table à droite
        JPanel mainPanel = new JPanel(new BorderLayout(15, 10));

        // === GAUCHE : Formulaire ===
        JPanel leftPanel = createFormPanel();

        // === DROITE : Recherche + Table ===
        JPanel rightPanel = createTablePanel();

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        chargerDonnees();
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Informations Client"));
        panel.setPreferredSize(new Dimension(300, 0));

        // Champs du formulaire
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        txtNom = new JTextField(15);
        txtPrenom = new JTextField(15);
        txtTel = new JTextField(15);
        txtEmail = new JTextField(15);

        // Nom
        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(new JLabel("Nom :"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(txtNom, gbc);

        // Prénom
        gbc.gridx = 0; gbc.gridy = 1;
        fieldsPanel.add(new JLabel("Prénom :"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(txtPrenom, gbc);

        // Téléphone
        gbc.gridx = 0; gbc.gridy = 2;
        fieldsPanel.add(new JLabel("Téléphone :"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(txtTel, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 3;
        fieldsPanel.add(new JLabel("Email :"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(txtEmail, gbc);

        // Photo
        gbc.gridx = 0; gbc.gridy = 4;
        fieldsPanel.add(new JLabel("Photo :"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(createPhotoPanel(), gbc);

        // Boutons
        JPanel btnPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcBtn = new GridBagConstraints();
        gbcBtn.insets = new Insets(5, 5, 5, 5);
        gbcBtn.fill = GridBagConstraints.HORIZONTAL;
        gbcBtn.weightx = 1.0;

        JButton btnAjouter = new JButton("Ajouter");
        JButton btnModifier = new JButton("Modifier");
        JButton btnSupprimer = new JButton("Supprimer");
        JButton btnVider = new JButton("Vider champs");

        gbcBtn.gridx = 0; gbcBtn.gridy = 0;
        btnPanel.add(btnAjouter, gbcBtn);
        gbcBtn.gridx = 1;
        btnPanel.add(btnModifier, gbcBtn);
        gbcBtn.gridx = 0; gbcBtn.gridy = 1;
        btnPanel.add(btnSupprimer, gbcBtn);
        gbcBtn.gridx = 1;
        btnPanel.add(btnVider, gbcBtn);

        btnAjouter.addActionListener(e -> ajouter());
        btnModifier.addActionListener(e -> modifier());
        btnSupprimer.addActionListener(e -> supprimer());
        btnVider.addActionListener(e -> viderChamps());

        panel.add(fieldsPanel, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPhotoPanel() {
        JPanel photoPanel = new JPanel(new BorderLayout(5, 5));

        lblPhotoPreview = new JLabel();
        lblPhotoPreview.setPreferredSize(new Dimension(100, 100));
        lblPhotoPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblPhotoPreview.setHorizontalAlignment(JLabel.CENTER);
        lblPhotoPreview.setText("Aucune photo");

        JButton btnChoisirPhoto = new JButton("Choisir");
        btnChoisirPhoto.addActionListener(e -> choisirPhoto());

        JButton btnSupprimerPhoto = new JButton("Supprimer");
        btnSupprimerPhoto.addActionListener(e -> {
            cheminPhoto = "";
            afficherPhoto("");
        });

        JPanel photoBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        photoBtns.add(btnChoisirPhoto);
        photoBtns.add(btnSupprimerPhoto);

        photoPanel.add(lblPhotoPreview, BorderLayout.CENTER);
        photoPanel.add(photoBtns, BorderLayout.SOUTH);

        return photoPanel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Liste des Clients"));

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
            chargerDonnees();
        });

        txtRecherche.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                rechercher();
            }
        });

        // Table
        model = new DefaultTableModel(new Object[]{"ID", "Nom", "Prénom", "Téléphone", "Email", "Photo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(25);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                remplirFormulaire();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void choisirPhoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg", "gif"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File fichier = fileChooser.getSelectedFile();
            cheminPhoto = fichier.getAbsolutePath();
            afficherPhoto(cheminPhoto);
        }
    }

    private void afficherPhoto(String chemin) {
        if (chemin != null && !chemin.isEmpty()) {
            File f = new File(chemin);
            if (f.exists()) {
                ImageIcon icon = new ImageIcon(new ImageIcon(chemin)
                        .getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                lblPhotoPreview.setIcon(icon);
                lblPhotoPreview.setText("");
            } else {
                lblPhotoPreview.setIcon(null);
                lblPhotoPreview.setText("Photo introuvable");
            }
        } else {
            lblPhotoPreview.setIcon(null);
            lblPhotoPreview.setText("Aucune photo");
        }
    }

    private String copierPhoto(String cheminOriginal) {
        if (cheminOriginal == null || cheminOriginal.isEmpty()) {
            return "";
        }
        try {
            File source = new File(cheminOriginal);
            if (!source.exists()) {
                return cheminOriginal;
            }

            String dossierPhotos = "photos_clients/";
            new File(dossierPhotos).mkdirs();

            String nouveauNom = System.currentTimeMillis() + "_" + source.getName();
            File destination = new File(dossierPhotos + nouveauNom);

            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return destination.getPath();
        } catch (IOException e) {
            e.printStackTrace();
            return cheminOriginal;
        }
    }

    private void chargerDonnees() {
        model.setRowCount(0);
        List<Client> clients = gestionClient.listerClients();
        for (Client c : clients) {
            String photoStatus = (c.getPhoto() != null && !c.getPhoto().isEmpty()) ? "Oui" : "Non";
            model.addRow(new Object[]{c.getId(), c.getNom(), c.getPrenom(), c.getTelephone(), c.getEmail(), photoStatus});
        }
    }

    private void rechercher() {
        String recherche = txtRecherche.getText().trim().toLowerCase();
        model.setRowCount(0);

        List<Client> clients = gestionClient.listerClients();
        for (Client c : clients) {
            String nomClient = (c.getNom() != null) ? c.getNom().toLowerCase() : "";
            String prenomClient = (c.getPrenom() != null) ? c.getPrenom().toLowerCase() : "";

            if (recherche.isEmpty() || nomClient.contains(recherche) || prenomClient.contains(recherche)) {
                String photoStatus = (c.getPhoto() != null && !c.getPhoto().isEmpty()) ? "Oui" : "Non";
                model.addRow(new Object[]{c.getId(), c.getNom(), c.getPrenom(), c.getTelephone(), c.getEmail(), photoStatus});
            }
        }
    }

    private void ajouter() {
        if (!validerChamps())
            return;

        Client c = new Client();
        c.setNom(txtNom.getText());
        c.setPrenom(txtPrenom.getText());
        c.setTelephone(txtTel.getText());
        c.setEmail(txtEmail.getText());
        c.setPhoto(copierPhoto(cheminPhoto));

        try {
            gestionClient.ajouterClient(c);
            chargerDonnees();
            viderChamps();
            JOptionPane.showMessageDialog(this, "Client ajouté !");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }

    private void modifier() {
        if (selectedClientId == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un client");
            return;
        }
        if (!validerChamps())
            return;

        Client c = gestionClient.getClient(selectedClientId);
        c.setNom(txtNom.getText());
        c.setPrenom(txtPrenom.getText());
        c.setTelephone(txtTel.getText());
        c.setEmail(txtEmail.getText());

        if (!cheminPhoto.isEmpty()) {
            c.setPhoto(copierPhoto(cheminPhoto));
        }

        gestionClient.modifierClient(c);
        chargerDonnees();
        viderChamps();
        JOptionPane.showMessageDialog(this, "Client modifié !");
    }

    private void supprimer() {
        if (selectedClientId == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un client");
            return;
        }

        int rep = JOptionPane.showConfirmDialog(this, "Voulez-vous supprimer ce client ?", "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (rep == JOptionPane.YES_OPTION) {
            gestionClient.supprimerClient(selectedClientId);
            chargerDonnees();
            viderChamps();
        }
    }

    private void remplirFormulaire() {
        int row = table.getSelectedRow();
        if (row != -1) {
            selectedClientId = (int) model.getValueAt(row, 0);
            txtNom.setText((String) model.getValueAt(row, 1));
            txtPrenom.setText((String) model.getValueAt(row, 2));
            txtTel.setText((String) model.getValueAt(row, 3));
            txtEmail.setText((String) model.getValueAt(row, 4));

            Client c = gestionClient.getClient(selectedClientId);
            if (c != null && c.getPhoto() != null) {
                cheminPhoto = c.getPhoto();
                afficherPhoto(cheminPhoto);
            } else {
                cheminPhoto = "";
                afficherPhoto("");
            }
        }
    }

    private void viderChamps() {
        txtNom.setText("");
        txtPrenom.setText("");
        txtTel.setText("");
        txtEmail.setText("");
        cheminPhoto = "";
        afficherPhoto("");
        selectedClientId = -1;
        table.clearSelection();
    }

    private boolean validerChamps() {
        if (txtNom.getText().isEmpty() || txtTel.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nom et Téléphone obligatoires");
            return false;
        }
        return true;
    }
}