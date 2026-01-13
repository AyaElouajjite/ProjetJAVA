package presentation;

import javax.swing.*;
import java.awt.*;

import dao.Proprietaire;
import metier.GestionUtilisateurs;
import metier.IGestionUtilisateurs;
import exception.DuplicateException;
import util.PasswordUtils;

public class RegisterFrame extends JFrame {

    private JTextField txtNom, txtPrenom, txtCin, txtEmail, txtTel, txtAdresse;
    private JPasswordField txtMdp;
    private JButton btnRegister, btnRetour;
    private IGestionUtilisateurs gestionUtilisateurs;

    // Couleurs
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color BACKGROUND = new Color(245, 245, 245);
    private static final Color WHITE = Color.WHITE;

    public RegisterFrame() {
        gestionUtilisateurs = new GestionUtilisateurs();

        setTitle("Inscription");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND);

        JPanel registerPanel = new JPanel(new GridBagLayout());
        registerPanel.setBackground(WHITE);
        registerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        registerPanel.setPreferredSize(new Dimension(500, 600));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Titre
        JLabel titleLabel = new JLabel("Créer un compte");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        registerPanel.add(titleLabel, gbc);

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = 1;

        // Nom
        gbc.gridx = 0;
        gbc.gridy = 1;
        registerPanel.add(new JLabel("Nom *"), gbc);
        gbc.gridx = 1;
        txtNom = new JTextField(20);
        registerPanel.add(txtNom, gbc);

        // Prénom
        gbc.gridx = 0;
        gbc.gridy = 2;
        registerPanel.add(new JLabel("Prénom"), gbc);
        gbc.gridx = 1;
        txtPrenom = new JTextField(20);
        registerPanel.add(txtPrenom, gbc);

        // CIN
        gbc.gridx = 0;
        gbc.gridy = 3;
        registerPanel.add(new JLabel("CIN *"), gbc);
        gbc.gridx = 1;
        txtCin = new JTextField(20);
        registerPanel.add(txtCin, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 4;
        registerPanel.add(new JLabel("Email *"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        registerPanel.add(txtEmail, gbc);

        // Téléphone
        gbc.gridx = 0;
        gbc.gridy = 5;
        registerPanel.add(new JLabel("Téléphone"), gbc);
        gbc.gridx = 1;
        txtTel = new JTextField(20);
        registerPanel.add(txtTel, gbc);

        // Adresse
        gbc.gridx = 0;
        gbc.gridy = 6;
        registerPanel.add(new JLabel("Adresse"), gbc);
        gbc.gridx = 1;
        txtAdresse = new JTextField(20);
        registerPanel.add(txtAdresse, gbc);

        // Mot de passe
        gbc.gridx = 0;
        gbc.gridy = 7;
        registerPanel.add(new JLabel("Mot de passe *"), gbc);
        gbc.gridx = 1;
        txtMdp = new JPasswordField(20);
        registerPanel.add(txtMdp, gbc);

        // Note
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        JLabel noteLabel = new JLabel("* Champs obligatoires");
        noteLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        noteLabel.setForeground(Color.GRAY);
        registerPanel.add(noteLabel, gbc);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(WHITE);

        btnRetour = new JButton("Retour");
        btnRetour.setForeground(PRIMARY_COLOR);
        btnRetour.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        btnRegister = new JButton("S'inscrire");
        btnRegister.setBackground(PRIMARY_COLOR);
        btnRegister.setForeground(Color.WHITE);
        btnRegister.addActionListener(e -> inscrire());

        buttonPanel.add(btnRetour);
        buttonPanel.add(btnRegister);

        gbc.gridy = 9;
        registerPanel.add(buttonPanel, gbc);

        mainPanel.add(registerPanel);
        add(mainPanel);
    }

    private void inscrire() {

        // Champs obligatoires
        if (txtNom.getText().trim().isEmpty() ||
            txtCin.getText().trim().isEmpty() ||
            txtEmail.getText().trim().isEmpty() ||
            txtMdp.getPassword().length == 0) {

            JOptionPane.showMessageDialog(this,
                    "Veuillez remplir tous les champs obligatoires",
                    "Erreur",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validation email
        String email = txtEmail.getText().trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this,
                    "Adresse email invalide",
                    "Erreur",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Hashage du mot de passe
        String motDePasseHash = PasswordUtils.hasherMotDePasse(new String(txtMdp.getPassword()));

        Proprietaire p = Proprietaire.builder()
                .nom(txtNom.getText().trim())
                .prenom(txtPrenom.getText().trim())
                .CIN(txtCin.getText().trim())
                .email(email)
                .numTel(txtTel.getText().trim())
                .motDePasse(motDePasseHash)  // Mot de passe hashé
                .actif(true)
                .build();

        try {
            gestionUtilisateurs.inscriptionProprietaire(p);

            int choix = JOptionPane.showConfirmDialog(
                    this,
                    "✅ Compte créé avec succès.\nVoulez-vous vous connecter ?",
                    "Succès",
                    JOptionPane.YES_NO_OPTION
            );

            if (choix == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }

        } catch (DuplicateException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Erreur d'inscription",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "❌ Erreur inattendue. Veuillez réessayer.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterFrame().setVisible(true));
    }
}