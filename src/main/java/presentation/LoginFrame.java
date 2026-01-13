package presentation;

import javax.swing.*;
import java.awt.*;

import dao.Proprietaire;
import dao.Reparateur;
import dao.Utilisateurs;
import metier.GestionUtilisateurs;
import metier.IGestionUtilisateurs;

public class LoginFrame extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtMdp;
    private JButton btnLogin, btnRegister;

    private IGestionUtilisateurs gestionUtilisateurs;

    // Couleurs
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color BACKGROUND = new Color(245, 245, 245);
    private static final Color WHITE = Color.WHITE;

    public LoginFrame() {

        gestionUtilisateurs = new GestionUtilisateurs();

        setTitle("Connexion");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        initUI();
    }

    private void initUI() {

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND);

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBackground(WHITE);
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        loginPanel.setPreferredSize(new Dimension(400, 350));

        JLabel titleLabel = new JLabel("Connexion");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(titleLabel);
        loginPanel.add(Box.createVerticalStrut(30));

        // EMAIL
        JLabel lblEmail = new JLabel("Email");
        lblEmail.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(lblEmail);
        loginPanel.add(Box.createVerticalStrut(5));

        txtEmail = new JTextField();
        txtEmail.setMaximumSize(new Dimension(300, 35));
        loginPanel.add(txtEmail);
        loginPanel.add(Box.createVerticalStrut(15));

        // MOT DE PASSE
        JLabel lblMdp = new JLabel("Mot de passe");
        lblMdp.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(lblMdp);
        loginPanel.add(Box.createVerticalStrut(5));

        txtMdp = new JPasswordField();
        txtMdp.setMaximumSize(new Dimension(300, 35));
        loginPanel.add(txtMdp);
        loginPanel.add(Box.createVerticalStrut(25));

        // BOUTON LOGIN
        btnLogin = new JButton("Se connecter");
        btnLogin.setPreferredSize(new Dimension(300, 40));
        btnLogin.setMaximumSize(new Dimension(300, 40));
        btnLogin.setBackground(PRIMARY_COLOR);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> connecter());
        loginPanel.add(btnLogin);

        loginPanel.add(Box.createVerticalStrut(10));

        // BOUTON REGISTER
        btnRegister = new JButton("Créer un compte");
        btnRegister.setPreferredSize(new Dimension(300, 40));
        btnRegister.setMaximumSize(new Dimension(300, 40));
        btnRegister.setBackground(Color.WHITE);
        btnRegister.setForeground(PRIMARY_COLOR);
        btnRegister.setFont(new Font("Arial", Font.PLAIN, 14));
        btnRegister.setFocusPainted(false);
        btnRegister.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2));
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });
        loginPanel.add(btnRegister);

        mainPanel.add(loginPanel);
        add(mainPanel);
    }

    // =========================
    // AUTHENTIFICATION
    // =========================
    private void connecter() {

        String email = txtEmail.getText().trim();
        String mdp = new String(txtMdp.getPassword());

        if (email.isEmpty() || mdp.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez remplir tous les champs",
                    "Erreur",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Utilisateurs user =
                gestionUtilisateurs.seConnecter(email, mdp);

        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "Email ou mot de passe incorrect",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 🔁 REDIRECTION SELON LE RÔLE
        if (user instanceof Proprietaire) {

            new EspaceProprietaireFrame((Proprietaire) user)
                    .setVisible(true);

        } else if (user instanceof Reparateur) {

            new EspaceReparateurFrame((Reparateur) user)
                    .setVisible(true);
        }

        dispose();
    }
}
